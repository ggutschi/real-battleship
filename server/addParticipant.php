<?php

require_once('config.php');

$conn = mysql_connect($db_host, $db_user, $db_pwd) OR die('Error connecting to mysql database');
mysql_select_db($db_name);

$android_id = htmlspecialchars(stripslashes($_REQUEST['android_id']));
$inet_address = htmlspecialchars(stripslashes($_REQUEST['inet_address']));
$challenge_id = htmlspecialchars(stripslashes($_REQUEST['challenge_id']));
$nickname = htmlspecialchars(stripslashes($_REQUEST['nickname']));
// || empty($nickname)
if(empty($challenge_id) || empty($inet_address) || empty($android_id))
{
	echo "Fehlende Parameter.";
	exit;
}

$result = mysql_query("SELECT * from challenges where id=" . $challenge_id);

if (mysql_num_rows($result) == 0)
{
	echo "Challenge mit der id " . $challenge_id . " nicht vorhanden.";
	exit;
}

$challenge = mysql_fetch_assoc($result);
if (is_array($challenge))
{
	// challenge gefunden
	// pruefe ob teilnehmer nicht schon vorhanden ist
	$res_participant = mysql_query("SELECT * from participants where challenge_id=" . $challenge_id . " AND android_id='" . $android_id . "'");

	if (mysql_num_rows($res_participant) <= 0) 
	{ 
		// teilnehmer nicht gefunden, insert
		mysql_query("INSERT INTO participants (challenge_id, inet_addr, android_id, nickname) VALUES ('" . $challenge_id . "', '" . $inet_address . "', '" . $android_id . "', '" . $nickname . "')");
		echo "OK";
	}
	else
	{ 
		// teilnehmer gefunden, update
		mysql_query("UPDATE participants SET inet_addr='" . $inet_address . "' where android_id = '" . $android_id . "' AND challenge_id = " . $challenge_id);
		echo "OK";
	}
	mysql_free_result($res_participant);
}

mysql_free_result($result);
mysql_close($conn);?>