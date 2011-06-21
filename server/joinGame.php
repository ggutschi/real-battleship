<?php

require_once('config.php');

$conn = mysql_connect($db_host, $db_user, $db_pwd) OR die('Error connecting to mysql database');
mysql_select_db($db_name);

$android_id = htmlspecialchars(stripslashes($_GET['android_id']));
$challenge_id = htmlspecialchars(stripslashes($_GET['challenge_id']));
$ip_address = htmlspecialchars(stripslashes($_GET['ip_address']));

if(empty($challenge_id) || empty($android_id) || empty($ip_address))
{
	echo "Fehlende Parameter.";
	exit;
}


$result = mysql_query("SELECT * FROM participants WHERE challenge_id = " . $challenge_id . " and android_id = '" . $android_id . "'");

if (mysql_num_rows($result) == 0)
{
	echo "NOK";
	exit;
}

// update ip address and set participant to active-state
mysql_query("UPDATE participants SET active=1, inet_addr='" . $ip_address . "' where challenge_id = " . $challenge_id . " and android_id = '" . $android_id . "'");

// send response (either "OK" if peer is first active player or list of active players)

$result = mysql_query("SELECT * FROM participants WHERE challenge_id = " . $challenge_id . " and active = 1 ");

if (mysql_num_rows($result) == 1)
{
	echo "OK";
	// start listening for other peers
	require_once('peerListener.php');
	exit;
}
else
{
	// more than one player
	$response = array();
	//$i = 0;
	while($row = mysql_fetch_array($result))
	{
		array_push($response, $row['inet_addr']);	
	}

	echo json_encode($response);  
}


mysql_close($conn); 

?>