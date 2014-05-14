<?php

$host = "localhost"; // Host MySQL
$user = "apps_sql"; // Usuario MySQL
$pass = "etsi123456"; // Contrasena MySQL
$database = "apps_sql"; // Base de datos MySQl
$conn = mysql_connect($host, $user, $pass); 
mysql_select_db($database);

?>