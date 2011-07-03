<?php

require_once('config.php');

$challenge_id = htmlspecialchars(stripslashes($_POST['challenge_id']));

if(!isset($challenge_id))
{
	echo "Fehlende Parameter. challenge_id: " . $_POST['challenge_id'];
	exit;
}

$response = array();

$conn = mysql_connect($db_host, $db_user, $db_pwd) OR die('Error connecting to mysql database');
mysql_select_db($db_name);

$result = mysql_query("SELECT s.score, p.nickname FROM scores s
                       inner join participants p on (p.id=s.participant_id)
                       inner join challenges c on (c.id = p.challenge_id)
                       where c.id = " . $challenge_id . "
                       order by s.score desc");

$i = 0;

while($row = mysql_fetch_array($result))
  {
    $response[$i] = array('nickname' => $row['nickname'],
                          'score' => $row['score']);
    $i++; 
  }
  
echo json_encode($response);  
mysql_close($conn);

?> 