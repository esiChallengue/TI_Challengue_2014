         <?     include(getcwd()."/config.php");
                $tracking_number_get = $_GET["tracking_number"];
                $conn = mysql_connect($host, $user, $pass); mysql_select_db($database);
                $result = mysql_query("SELECT * FROM registros WHERE tracking_number='$tracking_number_get' ORDER BY id_track DESC LIMIT 1", $conn);
                if ($row = mysql_fetch_array($result)){ do { 
                //Calculos de fuerzas G a grados
                $radx = floatval($row["xaxis"]);
                $rady = floatval($row["yaxis"]);
                $radz = floatval($row["zaxis"]);
                $radx = -$radx*100;
                $rady = -$rady*100;
                $radz = -$radz*100;
                
                $gradosX = rad2deg(atan2($rady, $radz) + pi());	
                $gradosY = rad2deg(atan2($radx, $radz) + pi());	
                $gradosZ = rad2deg(atan2($rady, $radx) + pi());	
                ?>
             
                        		    <strong>Temperatura: </strong><? echo $row["temperatura"]; ?> &ordm;C<br />
                                <strong>x-axis: </strong><? echo $row["xaxis"]; ?> G | <? echo $radx; ?> | <? echo $gradosX." &ordm;"; ?><br />
                                <strong>y-axis: </strong><? echo $row["yaxis"]; ?> G | <? echo $rady; ?> | <? echo $gradosY." &ordm;"; ?><br />
                                <strong>z-axis: </strong><? echo $row["zaxis"]; ?> G | <? echo $radz; ?> | <? echo $gradosZ." &ordm;"; ?><br /><br />
                                <strong>AVISO: </strong>No se han producido vuelcos<br />
                                <strong>AVISO: </strong>No se han alcanzado temperaturas criticas<br /><br />
   
                                <button type="button" name="botonGenerarAnimacion" id="botonGenerarAnimacion" style="width:370px;">Generar animacion</button>

           <?} while ($row = mysql_fetch_array($result)); } ?>      
      