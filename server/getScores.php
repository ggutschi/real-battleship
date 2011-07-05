<?php

require_once('config.php');

$challenge_id = htmlspecialchars(stripslashes($_REQUEST['challenge_id']));

if(!isset($challenge_id))
{
	echo "Fehlende Parameter. challenge_id: " . $_REQUEST['challenge_id'];
	exit;
}

$response = array();

$conn = mysql_connect($db_host, $db_user, $db_pwd) OR die('Error connecting to mysql database');
mysql_select_db($db_name);

$result = mysql_query("SELECT s.score, p.* FROM scores s
                       inner join participants p on (p.id=s.participant_id)
                       inner join challenges c on (c.id = p.challenge_id)
                       where c.id = " . $challenge_id . "
                       order by s.score desc");

$i = 0;

while($row = mysql_fetch_array($result))
  {
    $response[$i]['score'] = $row['score'];
    $response[$i]['user'] = array();
    $response[$i]['user']['nickname'] = $row['nickname'];
    $response[$i]['user']['android_id'] = $row['android_id'];
    $response[$i]['user']['inet_addr'] = $row['inet_addr'];
    $i++; 
  }
  
echo json_encode($response);  
mysql_close($conn);

?> 