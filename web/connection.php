<?php
	$username="mscray1";  
    $password="qazZAQ123321";  
    $db_name="softwear"; 
    $host = "198.71.227.92";  
     
    $conn = mysql_connect($host, $username, $password)or die("cannot connect"); 
    mysql_select_db($db_name)or die("cannot select DB");
?>
	