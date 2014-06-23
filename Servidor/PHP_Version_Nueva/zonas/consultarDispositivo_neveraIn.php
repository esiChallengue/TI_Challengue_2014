         <?     include(getcwd()."/config.php");
                $tracking_number_get = $_GET["tracking_number"];
                $conn = mysql_connect($host, $user, $pass); mysql_select_db($database);
                $result = mysql_query("SELECT * FROM registros WHERE tracking_number='$tracking_number_get' ORDER BY id_track DESC LIMIT 1", $conn);
                $result2 = mysql_query("SELECT MAX(cast(temperatura AS DECIMAL(10,2))) AS maxTemp, MIN(cast(temperatura AS DECIMAL(10,2))) AS minTemp 
                                       FROM registros WHERE tracking_number='$tracking_number_get'", $conn);
                $result3 = mysql_query("SELECT MAX(cast(xaxis AS DECIMAL(10,2))) AS maxXaxis, MIN(cast(xaxis AS DECIMAL(10,2))) AS minXaxis, 
                                       MAX(cast(yaxis AS DECIMAL(10,2))) AS maxYaxis, MIN(cast(yaxis AS DECIMAL(10,2))) AS minYaxis, 
                                       MAX(cast(zaxis AS DECIMAL(10,2))) AS maxZaxis, MIN(cast(zaxis AS DECIMAL(10,2))) AS minZaxis
                                       FROM registros WHERE tracking_number='$tracking_number_get'", $conn);
                if ($row = mysql_fetch_array($result)){ do { 
                if ($row2 = mysql_fetch_array($result2)){ $tempMax = $row2["maxTemp"]; $tempMin = $row2["minTemp"]; }
                if ($row3 = mysql_fetch_array($result3)){ 
                	$maxXaxis = $row3["maxXaxis"]; $minXaxis = $row3["minXaxis"]; 
                	$maxYaxis = $row3["maxYaxis"]; $minYaxis = $row3["minYaxis"];
                	$maxZaxis = $row3["maxZaxis"]; $minZaxis = $row3["minZaxis"];
                }
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
                
                $tempLimite = 4;
                ?>
             
                        		    <br /><strong>Temperatura actual: </strong><? echo $row["temperatura"]; ?> &ordm;C<br />
                        		    <strong>Temperatura max/min: </strong><? echo $tempMax; ?> &ordm;C / <? echo $tempMin; ?> &ordm;C<br />
                        		    <strong>Temperatura limite: </strong><? echo $tempLimite; ?> &ordm;C<br /><br />
                                <strong>x-axis: </strong><? echo $row["xaxis"]; ?> G | <? echo $radx; ?> | <? echo $gradosX." &ordm;"; ?><br />
                                <strong>y-axis: </strong><? echo $row["yaxis"]; ?> G | <? echo $rady; ?> | <? echo $gradosY." &ordm;"; ?><br />
                                <strong>z-axis: </strong><? echo $row["zaxis"]; ?> G | <? echo $radz; ?> | <? echo $gradosZ." &ordm;"; ?><br /><br />
                                <strong>Max/Min x-axis: </strong><? echo $maxXaxis; ?> | <? echo $minXaxis; ?><br />
                                <strong>Max/Min y-axis: </strong><? echo $maxYaxis; ?> | <? echo $minYaxis; ?><br />
                                <strong>Max/Min z-axis: </strong><? echo $maxZaxis; ?> | <? echo $minZaxis; ?><br /><br />
                                
                                <strong>AVISO: </strong><? echo detectarVolcado($maxXaxis, $minXaxis, $maxYaxis, $minYaxis, $maxZaxis, $minZaxis); ?>                              
                                
                                <? if(floatval($tempMax) > $tempLimite){ ?>
                                <strong>AVISO: </strong>Alcanzadas temperaturas criticas!!<br /><br />
                                <? }else{ ?>
                                <strong>AVISO: </strong>No se han alcanzado temperaturas criticas<br /><br />
                                <? } ?>
                                <button type="button" name="botonGenerarAnimacion" id="botonGenerarAnimacion" style="width:370px;">Generar animacion</button>

           <? } while ($row = mysql_fetch_array($result)); } ?>      
    
<? 

function detectarVolcado($maxXaxisGet, $minXaxisGet, $maxYaxisGet, $minYaxisGet, $maxZaxisGet, $minZaxisGet){
	
	$mensaje = "Se ha producido un vuelco!!<br>";
	$counter = 0;
	
	//Caso Reposo ideal, toda fuerza sobre el eje Z (vertical), x nada, y nada
	//Restamos en valor absoluto la posicion maxima y minima del eje Z, Y, X
	$difXaxis = $maxXaxisGet - $minXaxisGet;
	$difYaxis = $maxYaxisGet - $minYaxisGet;
	$difZaxis = $maxZaxisGet - $minZaxisGet;

	
	if($difZaxis > 0.8){ //Avisa de vuelco casi al llegar a los 90 grados de inclinacion
		//echo "Eje Z volcado!!".$difZaxis."<br>";
		//$mensaje = $mensaje."Eje Z volcado!!".$difZaxis."<br>";
	}else{ $counter++; }
	
  if($difYaxis > 0.8){
		//echo "Eje Y volcado!!".$difYaxis."<br>";
		//$mensaje = $mensaje."Eje Y volcado!!".$difYaxis."<br>";
	}else{ $counter++; }
	
	if($difXaxis > 0.8){
		//echo "Eje X volcado!!".$difXaxis."<br>";
		//$mensaje = $mensaje."Eje X volcado!!".$difXaxis."<br>";
	}else{ $counter++; }
	
	if($counter == 3){ $mensaje = "No se han producido vuelcos<br>"; }
	
	return $mensaje;
	
}

?>