<?php
 $zona= $_GET["zona"];
 $reload = $_GET["reload"];
 $tracking_number = $_GET["tracking_number"];
 $notas = $_GET["notas"];
 $nombreOperario = $_GET["nombreOperario"];
 $apellidosOperario = $_GET["apellidosOperario"];
 $dniOperario = $_GET["dniOperario"];
 $numeroTelefono = $_GET["telefono"];
 $latitud = $_GET["latitud"];
 $longitud = $_GET["longitud"];
 $temperatura = $_GET["temperatura"];
 $xAxis = $_GET["xAxis"];
 $yAxis = $_GET["yAxis"];
 $zAxis = $_GET["zAxis"];
 $cmd = $_GET["cmd"];
 $numeroCoordenadas = $_GET["numeroCoorMapa"];
 $refrescoMapa = $_GET["refrescoMapa"];
  
 include("config.php");
 
 if($reload == ""){
 
 include("cabecera.php"); 
 if (!$zona){include("zonas/central.php");}
 
 /***** Zona General *****/
 if ($zona == "consultarDispositivo"){ include("zonas/consultarDispositivo.php");}	
 if ($zona == "registrarEvento"){
 	//Metodo para recibir los parametros por medio de GET
 	if(($temperatura != "") AND ($temperatura != "desconocida")){
 	$insertar = "INSERT INTO registros (tracking_number, notas, nombreOperario, apellidosOperario, dniOperario, numeroTelefono, latitud, longitud, temperatura, xaxis, yaxis, zaxis) 
      VALUES ('$tracking_number', '$notas', '$nombreOperario', '$apellidosOperario', '$dniOperario', '$numeroTelefono', '$latitud', '$longitud', 
      '$temperatura', '$xAxis', '$yAxis', '$zAxis')";
      mysql_query($insertar, $conn);
   }   mysql_close($conn);
 	}
 
 include("piedepagina.php"); 
 
 }elseif($reload == "menuInfoDashboard"){
 	if($cmd == "addTrackRef"){	insertarTrackRef($tracking_number);	}
 	if($cmd == "deleteTrackRef"){	borrarTrackRef($tracking_number);	}
 	if($cmd == "resetTrackRef"){	resetearTrackRef($tracking_number);	}
 	include("zonas/consultarDispositivo_info.php");
 }elseif($reload == "menuNeveraDashboardIn"){
  include("zonas/consultarDispositivo_neveraIn.php");
 }elseif($reload == "menuLogDashboard"){
  include("zonas/consultarDispositivo_log.php");
 }elseif($reload == "menuMapaDashboard"){
 	if($cmd == "saveMapConfig"){	guardarMapConfig($tracking_number,  $numeroCoordenadas, $refrescoMapa);	}
  include("zonas/consultarDispositivo_mapa.php");
 }
 
 function insertarTrackRef($tracking){
 	include("config.php");
 	 	$insertar = "INSERT INTO track_estado (track_asociado, estado) VALUES ('$tracking', 'Operativo')";
    mysql_query($insertar, $conn);
    mysql_close($conn);
 }
 
  function borrarTrackRef($tracking){
 	include("config.php");
   $delete = "DELETE FROM track_estado WHERE track_asociado = '$tracking'";
   mysql_query($delete, $conn);
   mysql_close($conn); 
 }
 
  function resetearTrackRef($tracking){
 	include("config.php");
 	 	$resetear = "DELETE FROM registros WHERE tracking_number like '%$tracking%' ";
    mysql_query($resetear, $conn);
    mysql_close($conn);
 }
 
   function guardarMapConfig($tracking, $coordenadas, $refresco){
 	include("config.php");
   $update = "UPDATE track_estado Set numeroCoorMapa='$coordenadas', refrescoMapa='$refresco'  WHERE track_asociado='$tracking'";
   mysql_query($update, $conn);
   mysql_close($conn);
 }
 
?>