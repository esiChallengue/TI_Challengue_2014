         <!-- Account overview INICIO-->
            <div class="grid_5">
                <div class="module" style="width:400px;">
                        <h2><span>Informacion del operario responsable</span></h2>
                        
                        <div class="module-body">
                        
                        	<p>
                        		      <?  include(getcwd()."/config.php");
                        		          $tracking_number_get = $_GET["tracking_number"];
                                      $conn = mysql_connect($host, $user, $pass); mysql_select_db($database);
                                      $result = mysql_query("SELECT * FROM registros WHERE tracking_number='$tracking_number_get' ORDER BY id_track DESC LIMIT 1", $conn);
                                      if ($row = mysql_fetch_array($result)){ do { ?>
                                
                                         <strong>Nombre: </strong><? echo $row["nombreOperario"]; ?><br />
                                         <strong>Apellidos: </strong><? echo $row["apellidosOperario"]; ?><br />
                                         <strong>DNI: </strong><? echo $row["dniOperario"]; ?><br />
                                         <strong>Telefono: </strong><? echo $row["numeroTelefono"]; ?><br />
                                         <strong>Ultimo registro: </strong><? echo $row["hora"]; ?><br />
                                         <strong>Dispositivo: </strong><? echo $row["tracking_number"]; ?><br /><br />

                                  
                                  <?} while ($row = mysql_fetch_array($result)); } ?>   
                                   
                                  <?  $conn = mysql_connect($host, $user, $pass); mysql_select_db($database);
                                      $result = mysql_query("SELECT * FROM track_estado WHERE track_asociado='$tracking_number_get' ORDER BY id_estado DESC LIMIT 1", $conn);
                                      if ($row = mysql_fetch_array($result)){ do { 
                                
                                         if($row["estado"] == "Operativo"){
                                         echo "<strong>Estado: </strong>".$row["estado"]; ?>
                                     <img src="/images/track_delete.png" style="width:16px;height:16px;margin-left:10px;cursor:pointer;" 
                                     onClick="deleteTrack();" title="Eliminar ref." id="deleteTrackRef" />
                                     <img src="/images/track_reset.png" style="width:16px;height:16px;margin-left:10px;cursor:pointer;"
                                     onClick="resetTrack();" title="Reiniciar ref." id="resetTrackRef" />
                                         <? }elseif($row["estado"] == "No Operativo"){
                                         echo "<strong>Estado: </strong>".$row["estado"]; ?>
                                    <img src="/images/track_add.png" style="width:16px;height:16px;margin-left:10px;cursor:pointer;" 
                                    onClick="addTrack();" title="Agregar ref." id="addTrackRef" />
                                    <img src="/images/track_delete.png" style="width:16px;height:16px;margin-left:10px;cursor:pointer;" 
                                    onClick="deleteTrack();" title="Eliminar ref." id="deleteTrackRef" />
                                        
                                        <? }   } while ($row = mysql_fetch_array($result)); }else{ ?>                                   
                                    <strong>Estado: </strong>Neutral/Sin registrar
                                    <img src="/images/track_add.png" style="width:16px;height:16px;margin-left:10px;cursor:pointer;" 
                                    onClick="addTrack();" title="Agregar ref." id="addTrackRef" />
                                    <img src="/images/track_delete.png" style="width:16px;height:16px;margin-left:10px;cursor:pointer;" 
                                    onClick="deleteTrack();" title="Eliminar ref." id="deleteTrackRef" />
                                  <? } ?>  
                                  
                          </p>


                        </div>
                </div>
                <div style="clear:both;"></div>
            </div> <!-- End .grid_5 -->
            <!-- Account overview FINAL-->  
      