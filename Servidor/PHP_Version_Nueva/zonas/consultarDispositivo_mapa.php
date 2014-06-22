<?

//Leer configuraciones
include(getcwd()."/config.php"); 
$numeroCoordenadas = "25";
$refrescoMapa = "20";
 $conn = mysql_connect($host, $user, $pass); mysql_select_db($database);
  $result = mysql_query("SELECT * FROM track_estado WHERE track_asociado ='$tracking_number'", $conn);
  if ($row = mysql_fetch_array($result)){ do {
    $numeroCoordenadas = $row["numeroCoorMapa"];
    $refrescoMapa = $row["refrescoMapa"];
 } while ($row = mysql_fetch_array($result)); }  

function generarMapaTracking($tracking_number){
	//volvemos a hacer un include dentro de la funcion para definir las variables locales
  include(getcwd()."/config.php"); 
  $coordenadas_track = "";
  
  $conn = mysql_connect($host, $user, $pass); mysql_select_db($database);
  $result = mysql_query("SELECT * FROM registros WHERE tracking_number='$tracking_number' ORDER BY id_track DESC LIMIT 70", $conn);
  if ($row = mysql_fetch_array($result)){ do {
    $longitud = $row["longitud"];
    $latitud = $row["latitud"];
    $coordenadas_track = $coordenadas_track."\r".$longitud.",".$latitud."";
  } while ($row = mysql_fetch_array($result)); }  
  
	//Creamos un fichero con nombre aleatorio y lo devolvemos para actualizar la cache de google
	$content = file_get_contents(getcwd()."/extras/mapas_generados/track_base.KML"); 
	$pieces = explode("<coordinates>", $content);
	$contenido_total = $pieces[0]."<coordinates>".$coordenadas_track.$pieces[1];
	$num_aleatorio = rand(1,4000);
	$fichero = getcwd()."/extras/mapas_generados/track_".$tracking_number."_".$num_aleatorio.".KML";
	$fichero2 = "track_".$tracking_number."_".$num_aleatorio.".KML";
  $fp = fopen($fichero,"wb");
  fwrite($fp,$contenido_total);
  fclose($fp);
  
  return $fichero2;

}

?>

         <?     include(getcwd()."/config.php"); 
                $tracking_number_get = $_GET["tracking_number"];
                $idMapaKML = generarMapaTracking($tracking_number_get);
                $conn = mysql_connect($host, $user, $pass); mysql_select_db($database);
                $result = mysql_query("SELECT * FROM registros WHERE tracking_number='$tracking_number_get' ORDER BY id_track DESC LIMIT 1", $conn);
                if ($row = mysql_fetch_array($result)){ do { ?>
             
             
            <!-- Account overview INICIO-->
            <div class="grid_5">
                <div class="module" style="width:980px;">
                        <h2><span>Generador de mapas en tiempo real</span></h2>
                        
                        <div class="module-body">

                          <p>
                             <strong>Coordenadas a mostrar: </strong>
                               <select id="selectNumCoordenadasMapa">
                                 <option value="25" selected>Ultimas 25</option>
                                 <option value="50">Ultimas 50</option>
                                 <option value="100">Ultimas 100</option>
                                 <option value="250">Ultimas 250</option>
                                 <option value="700">Ultimas 700</option>
                                 <option value="todos">Todas</option>
                              </select>
                              
                              <span style="margin-left:50px;"><strong>Referescar mapa cada: </strong></span>
                               <select id="selectRefrescoMapa">
                                 <option value="25" selected>20 segundos</option>
                                 <option value="60">1 minuto</option>
                                 <option value="300">5 minutos</option>
                                 <option value="600">10 minutos</option>
                              </select>
                              
                              <span style="margin-left:50px;"><strong>Guardar configuracion: </strong> 
                               <img src="/images/saveConfig.png" style="width:16px;height:16px;cursor:pointer;" onClick="saveMapConfig();" title="Guardar conf." />     	
                              </span>
                          </p>
                          
                          <iframe src="https://maps.google.com/maps?q=http://apps.cavesquimo.es/extras/mapas_generados/<? echo $idMapaKML; ?>&amp;z=18&amp;t=m&amp;om=1&amp;ie=UTF8&amp;output=embed" width="900" height="600" frameborder="0" name="frameMapa" id="frameMapa" style="border:0"></iframe>	
                         </div> 

                        </div>
                </div>
                <div style="clear:both;"></div>
            </div> <!-- End .grid_5 -->
            <!-- Account overview FINAL-->
           <?} while ($row = mysql_fetch_array($result)); } ?>      
      