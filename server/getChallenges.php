<?php

require_once('config.php');

$response = array();

$conn = mysql_connect($db_host, $db_user, $db_pwd) OR die('Error connecting to mysql database');
mysql_select_db($db_name);

// AsText(`locationLeftTop`)
$result = mysql_query("SELECT id, name, active, location, X(`locationLeftTop`) as locationLeftTop_X, Y(`locationLeftTop`) as locationLeftTop_Y ,
                      X(`locationRightBottom`) as locationRightBottom_X, Y(`locationRightBottom`) as locationRightBottom_Y,
                      cellsX, cellsY FROM challenges");

$i = 0;

while($row = mysql_fetch_array($result))
  {
    //echo 'challenge name ' . " " . $row['name'] . '<br />';
    //echo 'challenge location ' . " " . $row['location'] .'<br />';    
    //echo 'challenge location ' . " " . $row['locationLeftTop_X'] .'<br />';
    //echo 'challenge location ' . " " . $row['locationRightBottom_X'] .'<br />';
    $response[$i] = array('id' => $row['id'],
                      'name' => $row['name'],
                      'location' => $row['location'],
                      'locationLeftTop' => array ('lat' => $row['locationLeftTop_X'], 'lon' => $row['locationLeftTop_Y'] ),
                      'locationRightBottom' => array ('lat' => $row['locationRightBottom_X'], 'lon' => $row['locationRightBottom_Y'] ),
                      'cellsX' => $row['cellsX'],
                      'cellsY' => $row['cellsY'],
                      'active' => (bool)$row['active'],
                      );
    
    $response[$i]['participants'] = array();
                                        
    $result_participants = mysql_query("SELECT android_id, inet_addr 
                                        FROM participants where challenge_id = " . $row['id'] . ";");
    $j = 0;
    while($row_participants = mysql_fetch_array($result_participants))
    {    
      //echo 'participant android id ' . " " . $row_participants['android_id'] .'<br />';
      //echo 'participant inet addr' . " " . $row_participants['inet_addr'] .'<br />';
      $response[$i]['participants'][$j] = array('android_id' => $row_participants['android_id'], 
                                                'inet_addr' => $row_participants['inet_addr']);
      $j++;
    }


    $result_ships = mysql_query("SELECT * FROM ships where challenge_id = " . $row['id'] . ";");

    $k = 0;
    $response[$i]['ships'] = array();
    while($row_ships = mysql_fetch_array($result_ships))
    {
	$response[$i]['ships'][$k] = array('id' => $row_ships['id'],
						'destroyed' => (boolean)$row_ships['destroyed'] );

    	$result_pos= mysql_query("SELECT `row`, `column`, `uncovered` 
                                        FROM ship_positions where ship_id = " . $row_ships['id'] . ";");
	$z = 0;
       $response[$i]['ships'][$k]['shippositions'] = array();
       while($row_pos = mysql_fetch_array($result_pos))
       {    
	      //echo 'ship position row ' . " " . $row_pos['row'] .'<br />';
	      //echo 'ship position col' . " " . $row_pos['column'] .'<br />';
	      $response[$i]['ships'][$k]['shippositions'][$z] = array('row' => $row_pos['row'],
                                                 'column' => $row_pos['column'],
							'uncovered' => (boolean)$row_pos['uncovered']);
	      $z++;
	}

	$k++;
     } // ships


    $i++;
  }

echo json_encode($response);  
mysql_close($conn); 
?>