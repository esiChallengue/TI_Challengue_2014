<?php
//Metodo para recibir los parametros por medio de GET
$tracking_number_get = $_GET["tracking_number"];
$notas_get = $_GET["notas"];
$cmd_get = $_GET["cmd"];
$titulo = "";

//incluimos fichero de configurar para conexiones BD y parametros fijos
include("config.php");

function generarMapaTracking($tracking_number){
	//volvemos a hacer un include dentro de la funcion para definir las variables locales
  include("config.php");
  $coordenadas_track = "";
  
  $conn = mysql_connect($host, $user, $pass); mysql_select_db($database);
  $result = mysql_query("SELECT * FROM registros ORDER BY id_track DESC LIMIT 25", $conn);
  if ($row = mysql_fetch_array($result)){ do {
  	$temp = $row["notas"];
    $cachos = explode("||", $temp);
    $latitud = $cachos[0];
    $longitud = $cachos[1];
    $coordenadas_track = $coordenadas_track."\r".$latitud.",".$longitud.",0";
  } while ($row = mysql_fetch_array($result)); }  
  
	//Creamos un fichero con nombre aleatorio y lo devolvemos para actualizar la cache de google
	$content = file_get_contents('track_base.KML');
	$pieces = explode("<coordinates>", $content);
	$contenido_total = $pieces[0]."<coordinates>".$coordenadas_track.$pieces[1];
	$num_aleatorio = rand(1,4000);
	$fichero = "maps_track/track_98377RF_".$num_aleatorio.".KML";
  $fp = fopen($fichero,"wb");
  fwrite($fp,$contenido_total);
  fclose($fp);
  
  return $fichero;
	
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

   $conn = mysql_connect($host, $user, $pass); mysql_select_db($database);
   $result = mysql_query("SELECT * FROM registros", $conn);
   if ($row = mysql_fetch_array($result)){ do {
    $titulo = $row["tracking_number"];
   } while ($row = mysql_fetch_array($result)); }
?>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>TrackingNevera -- EventsLog</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<script type="text/javascript" src="javascript/jquery-1.3.2.min.js" ></script>
<link rel="stylesheet" type="text/css" href="css/estilo.css" />

</head>
<body>
    <div id="header">
        <div id="header_left"><h1>TrackingNevera -- ID:<? echo $titulo; ?></h1></div>
    </div>

    <div id="content">
    <? $conn = mysql_connect($host, $user, $pass); mysql_select_db($database);
       $result = mysql_query("SELECT * FROM registros ORDER BY id_track DESC LIMIT 25", $conn);
       if ($row = mysql_fetch_array($result)){ do {
       echo "<p>".$row["id_track"]." - ".$row["notas"]." --".$row["hora"]."</p>";
       } while ($row = mysql_fetch_array($result)); } 
    ?>
    </div>
    
</body>
</html>