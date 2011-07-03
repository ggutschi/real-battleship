<?php

require_once('config.php');

$conn = mysql_connect($db_host, $db_user, $db_pwd) OR die('Error connecting to mysql database');
mysql_select_db($db_name);

// reset uncovered ship positions
mysql_query("UPDATE ship_positions SET uncovered=0");

// reset destroyed ships
mysql_query("UPDATE ships SET destroyed=0");

// delete scores
mysql_query("DELETE * from scores");

echo "OK";

?>