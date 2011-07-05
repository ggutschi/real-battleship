<?php
class PeerListener {
    private $address = '93.104.210.214';   
    private $port = 19423;         		// tcp port
    private $maxClients = 10;
    private static $JOIN_MESSAGE_TYPE = 'joined';
    private static $RELEASE_MESSAGE_TYPE = 'released';
    private static $UNCOVER_MESSAGE_TYPE = 'uncovered';
    private static $OK_MESSAGE = 'OK';
    private static $NOK_MESSAGE = 'NOK';
    private static $ALREADY_UNCOVERED_MESSAGE = 'aluncovered';
	  
    private $clients;
    private $sock = null;
    private $peers = array();
    private $mysql_conn = null;
 
    public function __construct() {
        // Set time limit to indefinite execution
        set_time_limit(0);
        error_reporting(E_ALL ^ E_NOTICE);
    }
    
    // Helper to break execution upon socket related error
    private function socket_error($msg)
    {
        $this->log($msg . socket_strerror(socket_last_error()));
        // Close sockets
        global $sock;
        @socket_close($sock);
        exit(1);
    }
 
    public function start() {
    
      // create mysql connection
      require_once('config.php');
      $this->mysql_conn = mysql_connect($db_host, $db_user, $db_pwd) OR die('Error connecting to mysql database');
      mysql_select_db($db_name);
    
      // Creating and binding socket
      $sock = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
      if (false === $sock) {
          socket_error('socket_create() failed');
      }

      socket_set_option($sock, SOL_SOCKET, SO_REUSEADDR, 1);

      if (false === socket_bind($sock, $this->address, $this->port)) {
          socket_error('socket_bind() failed');
      }
      
      if (false === socket_listen($sock, 5)) {
          socket_error('socket_listen() failed');
      }
      
      // Client connections' pool
      $pool = array($sock);
                       
      // Main cycle
      while (true) {
          $this->log('loop start');
          // Clean-up pool
          foreach ($pool as $conn_id => $conn) {
              if ( ! is_resource($conn)) {
                  unset($pool[$conn_id]);
              }
          }
      
          // Create a copy of pool for socket_select()
          $active = $pool;
      
          // Halt execution if socket_select() failed
          if (false === socket_select($active, $w = null, $e = null, null)) {
              socket_error('socket_select() failed');
          }
      
          // Register new client in the pool
          if (in_array($sock, $active)) {
              $conn = socket_accept($sock);
              if (is_resource($conn)) {
                  // Send welcome message
                  //$msg = PHP_EOL . 'WELCOME TO THE PHP SIMPLE SERVER' . PHP_EOL;
                  //socket_write($conn, $msg, strlen($msg));
                  $pool[] = $conn;
              }
              unset($active[array_search($sock, $active)]);
          }
      
          // Handle every active client
          foreach ($active as $conn) {
              $request = socket_read($conn, 2048, PHP_NORMAL_READ);
      
              // If connection is closed, remove it from pool and skip
              if (false === $request) {
                  unset($pool[array_search($conn, $pool)]);
                  continue;
              }
      
              $request = trim($request);
      
              // Skip to next if client tells nothing
              if (0 == strlen($request)) {
                  continue;
              }
      
              $this->log('GOT peer message: ' . $request);
              
              
		         if(!empty($request)) {
					     $dataArr = preg_split('/;/', $request);
					     if (is_array($dataArr)) {                       								
    								if (strcmp($dataArr[0], self::$JOIN_MESSAGE_TYPE) == 0) {
    									$this->log('new join message arrived');
    									print_r($dataArr);
                      $socketRessource = $conn;
                      //print_r($pool);					
          						$msg = $this->handleJoinMessage($dataArr, $socketRessource);
                    }
                    else if (strcmp($dataArr[0], self::$RELEASE_MESSAGE_TYPE) == 0) {
                      $this->log('new release message arrived');
                      print_r($dataArr);
                      $msg = $this->handleReleaseMessage($dataArr);
                    }
                    
                    else if (strcmp($dataArr[0], self::$UNCOVER_MESSAGE_TYPE) == 0) {
                      $this->log('new uncover message arrived');
                      $msg = $this->handleUncoverMessage($dataArr);
                    }
                    
                    $this->log('write message to client: ' . $msg);
		                socket_write($conn, $msg . "\r\n");
  
                }
            } 
      
              /*if (1 === preg_match('/quit|exit/i', $request)) {
                  socket_close($conn);
                  unset($pool[array_search($conn, $pool)]);
                  continue;
              }
      
              if (1 === preg_match('/stop|halt/i', $request)) {
                  break 2;
              }
      
              $response = md5($request) . PHP_EOL;
              socket_write($conn, $response, strlen($response));
              */
          }
      }
      
      // Finally close socket
      socket_close($sock);
}

private function handleUncoverMessage($dataArr) {
  $android_id = $dataArr[1]; // android_id;
  $challenge_id = $dataArr[2]; // challenge_id
  $uncovered_col = $dataArr[3]; // col
  $uncovered_row = $dataArr[4]; // row

  if(!isset($android_id) || !isset($challenge_id) || !isset($uncovered_row) || !isset($uncovered_col)) {
	 $msg = "Fehlende Parameter. android_id: " . $_POST['android_id'] . " challenge_id: " . $_POST['challenge_id'] . " row: " . $_POST['row'] . " col: " . $_POST['col'];
	 return $msg;
  }
  
  $result = mysql_query("SELECT * from challenges where id=" . $challenge_id);

  if (mysql_num_rows($result) == 0) {
	 $msg = "Challenge mit der id " . $challenge_id . " nicht vorhanden.";
	 mysql_close($conn); 
	 return $msg;
  }


  $res = mysql_query("select * from ships s 
			inner join challenges c on (c.id = s.challenge_id)
			inner join ship_positions sp on (s.id = sp.ship_id)
			where c.id = " . $challenge_id . " and c.active = 1 and sp.row = " . $uncovered_row . " and sp.column = " . $uncovered_col);

  $ship_position = mysql_fetch_assoc($res);

  if (is_array($ship_position))
  {
  	// ship position exists
  	if ($ship_position['uncovered'] != 1)
  	{
  		$this->log('in ship not uncovered');
  		// ship position not yet uncovered
  		mysql_query("UPDATE ship_positions SET uncovered=1 where id = " . $ship_position['id']);
  		
		// update score of user
		$result_part = mysql_query("SELECT p.id, p.challenge_id, s.score from participants p left join scores s on (s.participant_id = p.id) where p.android_id='" . $android_id . "' and p.challenge_id=" . $challenge_id);
	       $this->log("SELECT p.id, s.score from participants p left join scores s on (s.participant_id = p.id) where p.android_id='" . $android_id . "' and p.challenge_id=" . $challenge_id);
		$participant_score = mysql_fetch_assoc($result_part);	
	
              if (is_array($participant_score)) {
			$this->log('in is participant array');
			if (!is_null($participant_score['score']) && !empty($participant_score['score']))
				$currentScore = $participant_score['score'];		
			else
				$currentScore = 0;

			// increase score
			$currentScore+=10;
			$this->log('participant android id: ' . $android_id . ', current score: ' . $currentScore);

			// update score in database
			$result_checkscore = mysql_query("SELECT * from scores where participant_id = " . $participant_score['id'] . " and challenge_id = " . $challenge_id);
			if (mysql_num_rows($result_checkscore) == 0) 
 				mysql_query("INSERT INTO scores (challenge_id, participant_id, score) VALUES (" . $challenge_id . ", " . $participant_score['id'] . ", " . $currentScore . ");");			
			else
				mysql_query("UPDATE scores SET score=" . $currentScore . " where participant_id=" . $participant_score['id'] . " and challenge_id = " . $challenge_id);
			
		} 

  		$num_uncovered = mysql_query("select * from ship_positions sp where ship_id = " . $ship_position['ship_id'] . " and uncovered = 0");
  		$count_uncovered = mysql_num_rows($num_uncovered);
  
  		if ($count_uncovered < 1) {
  			mysql_query("UPDATE ships SET destroyed = 1 where id = " . $ship_position['ship_id']);
  		}		
  		$msg = self::$OK_MESSAGE;
  	}
  	else
  		$msg = self::$ALREADY_UNCOVERED_MESSAGE;
  }
  else
	 $msg = "Ship Position not found.";
	 
	 return $msg;
}

private function handleReleaseMessage($dataArr) {  
  if (count($dataArr) == 2) {
    $this->log('remove peer with android_id' . $dataArr[1]);
    
    foreach($this->peers as $challenge_id => $challenge_arr) {
      if (isset($challenge_arr[$dataArr[1]]) && !empty($challenge_arr[$dataArr[1]])) {
        unset($this->peers[$challenge_id][$dataArr[1]]); 
        $this->log('unset peer in challenge ' . $challenge_id); 
      }
    }
    
    $this->log('current peer array:');
    print_r($this->peers);
    return self::$OK_MESSAGE;    
  }
  else
    return self::$NOK_MESSAGE;
}

private function handleJoinMessage($dataArr, $socketRessource) {

  if ($this->checkInput($dataArr)) {
    // android_id in join message is set
    
    //if (!isset($this->peers[$dataArr[1]][$dataArr[2]]['response'])) {
      // peer did not receive an answer yet
      
      $this->peers[$dataArr[1]+0][$dataArr[2]]['challenge_id'] = $dataArr[1];
      socket_getpeername($socketRessource, $ip);
      $this->peers[$dataArr[1]+0][$dataArr[2]]['ipaddy'] = $ip;
      //$this->peers[$dataArr[1]+0][$dataArr[2]]['response'] = true;
      $this->peers[$dataArr[1]+0][$dataArr[2]]['android_id'] = $dataArr[2];
      $this->log("save new peer with android_id " . $dataArr[2]);
      $this->log("current peer array:");
      print_r($this->peers);
      // return ip addresses
      $ip_addresses = array();
      array_push($ip_addresses, array('ipaddy' => $this->address, 'android_id' => 'server'));
      foreach ($this->peers[$dataArr[1]] as $peer) {
        if (strcmp($this->peers[$dataArr[1]+0][$dataArr[2]]['ipaddy'], $peer['ipaddy']) != 0)
          array_push($ip_addresses, array('ipaddy' => $peer['ipaddy'], 'android_id' => $peer['android_id']));
        
      }
      
      
      $msg = json_encode($ip_addresses);
      return $msg;
    //}
    //else {
        // peer did already receive an answer from server
    //    $this->log('peer with android_id ' . $dataArr[2] . ' already registered');
    //   return self::$OK_MESSAGE;
    //}
  }
  else  {
    $this->log('invalid join message format. array length: ' . count($dataArr) . ' android_id:' . $dataArr[2]);
    return self::$NOK_MESSAGE; // invalid format of join message
  }  
}

private function checkInput($dataArr) {
    return (isset($dataArr[2]) && !empty($dataArr[2]) && isset($dataArr[1]) && !empty($dataArr[1]) && count($dataArr) == 3);
}

private function end() {
  if ($this->mysqlconn != NULL)
    mysql_close($this->mysqlconn);
}
  
private function log($msg) {
    echo "[".date('Y-m-d H:i:s')."] " . $msg . "\r\n";
}
}

$peerListener = new PeerListener();
$peerListener->start();
$peerListener->end();

?>