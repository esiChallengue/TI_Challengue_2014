<?

function generarMapaTracking($tracking_number, $numCoordenadas){
	//volvemos a hacer un include dentro de la funcion para definir las variables locales
  include(getcwd()."/config.php"); 
  $coordenadas_track = "";
  
  if($numCoordenadas == "-1"){ 
   $result = mysql_query("SELECT * FROM registros WHERE tracking_number='$tracking_number' ORDER BY id_track DESC", $conn);
  }else{
   $result = mysql_query("SELECT * FROM registros WHERE tracking_number='$tracking_number' ORDER BY id_track DESC LIMIT $numCoordenadas", $conn);
  }
  
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

                include(getcwd()."/config.php"); 
                $selCoord = "25";
                $selRefres = "20";
                $tracking_number_get = $_GET["tracking_number"];
                $result = mysql_query("SELECT * FROM registros WHERE tracking_number='$tracking_number_get' ORDER BY id_track DESC LIMIT 1", $conn);
                $result2 = mysql_query("SELECT * FROM track_estado WHERE track_asociado='$tracking_number_get' ORDER BY id_estado DESC LIMIT 1", $conn);
                if ($row = mysql_fetch_array($result)){ do { 
                if ($row2 = mysql_fetch_array($result2)){ $selCoord = $row2["numeroCoorMapa"]; $selRefres = $row2["refrescoMapa"]; } 
                $idMapaKML = generarMapaTracking($tracking_number_get, $selCoord);
                ?>
             
             
            <!-- Account overview INICIO-->
            <div class="grid_5">
                <div class="module" style="width:980px;">
                        <h2><span>Generador de mapas en tiempo real</span></h2>
                        
                        <div class="module-body">

                          <p>
                             <strong>Coordenadas a mostrar: </strong>
                               <select id="selectNumCoordenadasMapa">
                                 <option value="25" <? if($selCoord=="25"){ echo "selected"; } ?>>Ultimas 25</option>
                                 <option value="50" <? if($selCoord=="50"){ echo "selected"; } ?>>Ultimas 50</option>
                                 <option value="100" <? if($selCoord=="100"){ echo "selected"; } ?>>Ultimas 100</option>
                                 <option value="250" <? if($selCoord=="250"){ echo "selected"; } ?>>Ultimas 250</option>
                                 <option value="700" <? if($selCoord=="700"){ echo "selected"; } ?>>Ultimas 700</option>
                                 <option value="-1" <? if($selCoord=="-1"){ echo "selected"; } ?>>Todas</option>
                              </select>
                              
                              <span style="margin-left:50px;"><strong>Referescar mapa cada: </strong></span>
                               <select id="selectRefrescoMapa">
                                 <option value="20" <? if($selRefres=="20"){ echo "selected"; } ?>>20 segundos</option>
                                 <option value="60" <? if($selRefres=="60"){ echo "selected"; } ?>>1 minuto</option>
                                 <option value="300" <? if($selRefres=="300"){ echo "selected"; } ?>>5 minutos</option>
                                 <option value="600" <? if($selRefres=="600"){ echo "selected"; } ?>>10 minutos</option>
                              </select>
                              
                              <span style="margin-left:50px;"><strong>Guardar configuracion: </strong> 
                               <img src="/images/saveConfig.png" style="width:16px;height:16px;cursor:pointer;" onClick="saveMapConfig();" title="Guardar conf." />     	
                              </span>
                          </p>
                          
                          <iframe src="https://maps.google.com/maps?q=http://apps.cavesquimo.es/extras/mapas_generados/<? echo $idMapaKML; ?>&amp;z=17&amp;t=m&amp;om=1&amp;ie=UTF8&amp;output=embed" width="900" height="750" frameborder="0" name="frameMapa" id="frameMapa" style="border:0"></iframe>	
                         </div> 

                        </div>
                </div>
                <div style="clear:both;"></div>
            </div> <!-- End .grid_5 -->
            <!-- Account overview FINAL-->
           <? } while ($row = mysql_fetch_array($result)); } ?>      
      