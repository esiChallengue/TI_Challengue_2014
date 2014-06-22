           <!-- Account overview INICIO-->
            <div class="grid_5">
                <div class="module" style="width:1395px;margin-left:70px;">
                        <h2><span>Historial de registros llegados desde el movil del operador</span></h2>
                        
                        <div class="module-body">

                        
                            <div class="CSSTableGenerator" >
                <table >
                    <tr>
                    	  <td>ID</td>
                    	  <td>Referencia</td>
                        <td>Latitud</td>
                        <td>Longitud</td>
                        <td>Temperatura</td>
                        <td>X-axis</td>
                        <td>Y-axis</td>
                        <td>Z-axis</td>
                        <td>Telefono</td>
                        <td>Fecha y hora</td>
                    </tr>
                    
                <? include(getcwd()."/config.php"); 
                $tracking_number_get = $_GET["tracking_number"];
                $conn = mysql_connect($host, $user, $pass); mysql_select_db($database);
                $result = mysql_query("SELECT * FROM registros WHERE tracking_number='$tracking_number_get' ORDER BY id_track DESC LIMIT 25", $conn);
                if ($row = mysql_fetch_array($result)){ do { ?>
             
                    <tr>
                  	    <td><? echo $row["id_track"]; ?></td>
                  	    <td><? echo $row["tracking_number"]; ?></td>
                        <td><? echo $row["latitud"]; ?></td>
                        <td><? echo $row["longitud"]; ?></td>
                        <td><? echo $row["temperatura"]; ?></td>
                        <td><? echo $row["xaxis"]; ?></td>
                        <td><? echo $row["yaxis"]; ?></td>
                        <td><? echo $row["zaxis"]; ?></td>
                        <td><? echo $row["numeroTelefono"]; ?></td>
                        <td><? echo $row["hora"]; ?></td>
                    </tr>
                    
                <?} while ($row = mysql_fetch_array($result)); } ?>  
                </table>
            </div>


                        </div>
                </div>
                <div style="clear:both;"></div>
            </div> <!-- End .grid_5 -->
            <!-- Account overview FINAL-->