<?php
include("../config.php");

$submit_correos_add = $_POST["submit_correos_add"];
$submit_correos_delete = $_POST["submit_correos_delete"];
$submit_correos_update = $_POST["submit_correos_update"];

$titulo = $_POST["titulo"];
$referencia = $_POST["referencia"];
$email = $_POST["email"];
$id_referencia = $_POST["id_referencia"];

if (isset($submit_correos_add)) {
 $conn = mysql_connect($host, $user, $pass); mysql_select_db($database);
 $insertar = "INSERT INTO correos (titulo, referencia, email) VALUES ('$titulo', '$referencia', '$email')";
 mysql_query($insertar, $conn);
 $saved = 2;
}

if (isset($submit_correos_update)) {
 $conn = mysql_connect($host, $user, $pass); mysql_select_db($database);
 $update = "UPDATE correos Set estado='1' Where id_referencia='$id_referencia'";
 mysql_query($update, $conn);
 $saved = 2;
}

if (isset($submit_correos_delete)) {
 $conn = mysql_connect($host, $user, $pass); mysql_select_db($database);
 $delete = "DELETE FROM correos WHERE id_referencia = $id_referencia";
 mysql_query($delete, $conn);
 $saved = 2;
}
?>

<!-- Start contenido Engloba (info--acount y todos los modulos) -->
<div class="container_12">
<!-- Start contenido parte izquierda, sin incluir info-account -->
<div class="grid_7">
<!-- Start Correos Tracking -->
<div class="module"><h2><span>Correos - Tracking via email</span></h2> <div class="module-body">
   <? $result = mysql_query("SELECT *  FROM correos WHERE estado=0 ORDER BY id_referencia DESC", $conn);
      if ($row = mysql_fetch_array($result)){ do { ?>
     
     <span style="border:1px solid #CCC;display:block;padding:20px 20px 20px 30px;margin-bottom:5px;">
     <span style="font-size:15px;font-weight:bold;"><? echo $row["titulo"]." | ".$row["fecha"]; ?></span><br />
     <span style="font-size:12px;"><b>Nº referencia:</b> <? echo $row["referencia"]; ?></span><br />
     <span style="font-size:12px;"><b>Email:</b> <? echo $row["email"]; ?></span><br />
     <span style="font-size:12px;"><? echo $row["textocodigo"]; ?></span><br />
     <form method="POST" name="formucorreos_delete" enctype="multipart/form-data">
     <input type="hidden" name="id_referencia"  id="id_referencia" value="<? echo $row["id_referencia"]; ?>"/>
     <input type="submit" value=" Eliminar referencia " name="submit_correos_delete"/>
     <input type="submit" value=" Marcar como recibido " name="submit_correos_update"/>
     </form>
     </span>
     
   <? }while ($row = mysql_fetch_array($result)); } ?>  
   
   <br />Nuevo seguimiento (se enviará un email por cada cambio de estado)<hr /><br />
   <form method="POST" name="formucorreos" enctype="multipart/form-data">
   Producto: <input type="text" name="titulo" id="titulo" size="25"/>
   Nº seguimiento: <input type="text" name="referencia" id="referencia" />
   Email al que avisar: <input type="text" name="email" id="email" size="25" />
   <input type="submit" value=" Agregar referencia " name="submit_correos_add" />
   </form>
   
</div></div><div style="clear:both;"></div>
<!-- End Correos Tracking -->    

<!-- Start SVM-Android App -->
<div class="module"><h2><span>SVM -- Android app - Play-Google</span></h2> <div class="module-body">

<?php $conn = mysql_connect($host, $user, $pass);  mysql_select_db($database); 
$result = mysql_query("SELECT * FROM correos WHERE id_estatico='0'", $conn);
if ($row = mysql_fetch_array($result)){do {
?>

      
<?php } while ($row = mysql_fetch_array($result)); } ?>
</div></div><div style="clear:both;"></div>
<!-- End SVM-Android App  -->    
<!-- End Grid_7 -->
</div>

<!--Start Info-Account -->
<? include("info_cuenta.php"); ?>   
<!--End Info-Account -->

<!-- End contenido -->
<div style="clear:both;"></div>
</div>