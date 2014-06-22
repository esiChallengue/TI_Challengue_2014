    <script type="text/javascript" charset="utf-8" src="/js/pruebanev_edgePreload.js"></script>
    <style>.edgeLoad-EDGE-38908700 { visibility:visible; }</style>
	
		<div class="container_12">

            <!-- Dashboard InfoMenu -->
            <div class="grid_7" id="menuInfoDashboard">
             <? include("consultarDispositivo_info.php"); ?>           
                <div style="clear: both"></div>                
            </div> <!-- End .grid_7 -->
            
            <!-- Dashboard SistemaVuelco -->
            <div class="grid_7" id="menuNeveraDashboard">
            <? include("consultarDispositivo_nevera.php"); ?>
               <div style="clear: both"></div>                
            </div> <!-- End .grid_7 -->
             
             <!-- Dashboard GenerarMapa -->
             <div id="menuMapaDashboard" style="position:absolute;margin-left:430px;margin-top:0px;">
             <? include("consultarDispositivo_mapa.php"); ?>
               <div style="clear: both"></div> 
             </div>        
             
            <!-- Dashboard SistemaVuelco -->
            <div class="grid_7" id="menuLogDashboard">
            <? include("consultarDispositivo_log.php"); ?>
               <div style="clear: both"></div>                
            </div> <!-- End .grid_7 -->
             
          <div style="clear:both;"></div>
        </div> <!-- End .container_12 -->