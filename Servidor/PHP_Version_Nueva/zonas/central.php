		<div class="container_12">

            <!-- Dashboard icons -->
            <div class="grid_7">

            <? //Recorremos la base de datos para listar los diferentes numeros de tracking
            $conn = mysql_connect($host, $user, $pass); mysql_select_db($database);
            $result = mysql_query("SELECT DISTINCT tracking_number FROM registros", $conn);
            if ($row = mysql_fetch_array($result)){ do { $track = $row["tracking_number"];?>

            	<a href="?zona=consultarDispositivo&tracking_number=<? echo $track; ?>" class="dashboard-module">
                	<img src="images/<? echo devolverImagen($track); ?>" width="64" height="64" alt="edit" />
                	<span><? echo $track; ?></span>
              </a>
              
            <? } while ($row = mysql_fetch_array($result)); }   ?>
                                

                <div style="clear: both"></div>                
            </div> <!-- End .grid_7 -->
            
             <? include("info_cuenta.php"); ?>    
                     
          <div style="clear:both;"></div>
        </div> <!-- End .container_12 -->

<? function devolverImagen($tracking_number){
    $imagen = "tracking_gris.png";
    include("config.php");
    $conn = mysql_connect($host, $user, $pass); mysql_select_db($database);
    $result = mysql_query("SELECT estado FROM track_estado WHERE track_asociado='$tracking_number'", $conn);
    if ($row = mysql_fetch_array($result)){ do {
    
     if($row["estado"] == "Operativo"){
      $imagen = "tracking_verde.png";     	
     	 }elseif ($row["estado"] == "No Operativo"){
      $imagen = "tracking_rojo.png";     	 
     }         
    } while ($row = mysql_fetch_array($result)); }
	
	  return $imagen;
} ?>
	
