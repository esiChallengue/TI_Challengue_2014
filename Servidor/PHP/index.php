<?php
//Metodo para recibir los parametros por medio de GET
$tracking_number_get = $_GET["tracking_number"];
$notas_get = $_GET["notas"];
$cmd_get = $_GET["cmd"];
$titulo = "";
$idMapaKML = "";

//incluimos fichero de configurar para conexiones BD y parametros fijos
include("config.php");

function generarMapaTracking($tracking_number){
	//volvemos a hacer un include dentro de la funcion para definir las variables locales
  include("config.php");
  $coordenadas_track = "";
  
  $conn = mysql_connect($host, $user, $pass); mysql_select_db($database);
  $result = mysql_query("SELECT * FROM registros WHERE tracking_number='$tracking_number' ORDER BY id_track DESC LIMIT 70", $conn);
  if ($row = mysql_fetch_array($result)){ do {
  	$temp = $row["notas"];
    $cachos = explode("||", $temp);
    $latitud = $cachos[0];
    $longitud = $cachos[1];
    $coordenadas_track = $coordenadas_track."\r".$longitud.",".$latitud."";
  } while ($row = mysql_fetch_array($result)); }  
  
	//Creamos un fichero con nombre aleatorio y lo devolvemos para actualizar la cache de google
	$content = file_get_contents('track_base.KML');
	$pieces = explode("<coordinates>", $content);
	$contenido_total = $pieces[0]."<coordinates>".$coordenadas_track.$pieces[1];
	$num_aleatorio = rand(1,4000);
	$fichero = "maps_track/track_".$tracking_number."_".$num_aleatorio.".KML";
	$fichero2 = "track_".$tracking_number."_".$num_aleatorio.".KML";
  $fp = fopen($fichero,"wb");
  fwrite($fp,$contenido_total);
  fclose($fp);
  
  return $fichero2;
	
}

function registrarEvento($tracking_number, $notas){
	    //volvemos a hacer un include dentro de la funcion para definir las variables locales
      include("config.php");
      //insertamos en BD +id+cmd+timestamp+status
      $insertar = "INSERT INTO registros (tracking_number, notas) VALUES ('$tracking_number', '$notas')";
      mysql_query($insertar, $conn);
      mysql_close($conn);
}


if($cmd_get == "registrarEvento"){ registrarEvento($tracking_number_get, $notas_get);}
if($cmd_get == "consultarEventos"){ $titulo = "  ".$tracking_number_get; $idMapaKML = generarMapaTracking($tracking_number_get);}
?>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>TrackingNevera -- EventsLog</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<META HTTP-EQUIV="REFRESH" CONTENT="20">
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<script type="text/javascript" src="javascript/jquery-1.3.2.min.js" ></script>
<link rel="stylesheet" type="text/css" href="css/estilo.css" />

</head>
<body>
    <div id="header">
        <div id="header_left" onclick="window.location='?'"><h1>TrackingNevera -- ID:<? echo $titulo; ?></h1></div>
    </div>

    <div id="content">
    	
    <? if ($tracking_number_get == ""){
       //Recorremos la base de datos para listar los diferentes numeros de tracking
       $conn = mysql_connect($host, $user, $pass); mysql_select_db($database);
       $result = mysql_query("SELECT DISTINCT tracking_number FROM registros", $conn);
       if ($row = mysql_fetch_array($result)){ do { $track = $row["tracking_number"];?>
        <div id="trackRef" onclick="window.location='?cmd=consultarEventos&tracking_number=<? echo $track; ?>'">Dispositivo: <? echo $track; ?></div>
       <? } while ($row = mysql_fetch_array($result)); }       
     }else{
     	 $conn = mysql_connect($host, $user, $pass); mysql_select_db($database);
       $result = mysql_query("SELECT * FROM registros WHERE tracking_number='$tracking_number_get' ORDER BY id_track DESC LIMIT 25", $conn);
       if ($row = mysql_fetch_array($result)){ do {
       echo "<p>".$row["id_track"]." - ".$row["notas"]." --".$row["hora"]."</p>";
       } while ($row = mysql_fetch_array($result)); } ?>  	
       <div id="contentMapa">
       <iframe src="https://maps.google.com/maps?q=http:%2F%2Fapps.cavesquimo.es%2Fmaps_track%2F<? echo $idMapaKML; ?>&amp;t=k&amp;om=1&amp;ie=UTF8&amp;output=embed" width="600" height="500" frameborder="0" name="frameMapa" id="frameMapa" style="border:0"></iframe>	
       </div> 
       
     <? } ?>    
 
    </div>
    
</body>
</html>