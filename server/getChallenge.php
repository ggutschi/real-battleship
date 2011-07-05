<?php

require_once('config.php');

$challenge_id = htmlspecialchars(stripslashes($_REQUEST['challenge_id']));

if(!isset($challenge_id) || empty($challenge_id))
{
	echo "Fehlende Parameter. challenge_id: " . $_REQUEST['challenge_id'];
	exit;
}


$conn = mysql_connect($db_host, $db_user, $db_pwd) OR die('Error connecting to mysql database');
mysql_select_db($db_name);


$result = mysql_query("SELECT id, name, active, location, X(`locationLeftTop`) as locationLeftTop_X, Y(`locationLeftTop`) as locationLeftTop_Y ,
                      X(`locationRightBottom`) as locationRightBottom_X, Y(`locationRightBottom`) as locationRightBottom_Y,
                      cellsX, cellsY FROM challenges where id=" . $challenge_id);

$row = mysql_fetch_assoc($result);                     
$response = array('id' => $row['id'],
                      'name' => $row['name'],
                      'location' => $row['location'],
                      'locationLeftTop' => array ('lat' => $row['locationLeftTop_X'], 'lon' => $row['locationLeftTop_Y'] ),
                      'locationRightBottom' => array ('lat' => $row['locationRightBottom_X'], 'lon' => $row['locationRightBottom_Y'] ),
                      'cellsX' => $row['cellsX'],
                      'cellsY' => $row['cellsY'],
                      'active' => (bool)$row['active'],
                      );
                      

$response['participants'] = array();
                  
$result_participants = mysql_query("SELECT p.*, s.score from participants p left join scores s on (s.participant_id = p.id and s.challenge_id = p.challenge_id) 
				      where p.challenge_id=" . $challenge_id);

/*SELECT android_id, inet_addr 
                                    FROM participants where challenge_id = " . $challenge_id . ";");
*/

$j = 0;
while($row_participants = mysql_fetch_array($result_participants)) {    
  //echo 'participant android id ' . " " . $row_participants['android_id'] .'<br />';
  //echo 'participant inet addr' . " " . $row_participants['inet_addr'] .'<br />';
  $response['participants'][$j] = array('android_id' => $row_participants['android_id'], 
                                            'inet_addr' => $row_participants['inet_addr'],
						  'nickname'  => $row_participants['nickname'],
						  'score'     => $row_participants['score']);
  $j++;
}

$result_ships = mysql_query("SELECT * FROM ships where challenge_id = " . $challenge_id);

$k = 0;
$response['ships'] = array();
while($row_ships = mysql_fetch_array($result_ships)) {
	     $response['ships'][$k] = array('id' => $row_ships['id'],
						                          'destroyed' => (boolean)$row_ships['destroyed'] );

    	 $result_pos= mysql_query("SELECT `row`, `column`, `uncovered` 
                                        FROM ship_positions where ship_id = " . $row_ships['id'] . ";");
	     $z = 0;
       $response['ships'][$k]['shippositions'] = array();
       while($row_pos = mysql_fetch_array($result_pos))
       {    
	       //echo 'ship position row ' . " " . $row_pos['row'] .'<br />';
	       //echo 'ship position col' . " " . $row_pos['column'] .'<br />';
	       $response['ships'][$k]['shippositions'][$z] = array('row' => $row_pos['row'],
                                                 'column' => $row_pos['column'],
				                                         'uncovered' => (boolean)$row_pos['uncovered']);
	       $z++;
	     }     
	$k++;
}
echo json_encode($response);  
mysql_close($conn); 
?>