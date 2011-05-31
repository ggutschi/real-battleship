<?php

require_once('config.php');

$conn = mysql_connect($db_host, $db_user, $db_pwd) OR die('Error connecting to mysql database');
mysql_select_db($db_name);

$challenge_id = htmlspecialchars(stripslashes($_POST['challenge_id']));
$uncovered_row = htmlspecialchars(stripslashes($_POST['row']));
$uncovered_col = htmlspecialchars(stripslashes($_POST['col']));

if(!isset($challenge_id) || !isset($uncovered_row) || !isset($uncovered_col))
{
	echo "Fehlende Parameter. challenge_id: " . $_POST['challenge_id'] . " row: " . $_POST['row'] . " col: " . $_POST['col'];
	exit;
}

$result = mysql_query("SELECT * from challenges where id=" . $challenge_id);

if (mysql_num_rows($result) == 0)
{
	echo "Challenge mit der id " . $challenge_id . " nicht vorhanden.";
	exit;
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
		// ship position not yet uncovered
		mysql_query("UPDATE ship_positions SET uncovered=1 where id = " . $ship_position['id']);
		echo "OK";
	}
	else
		echo "NOK"; // already uncovered
}
else
	echo "Ship Position not found.";

?>
