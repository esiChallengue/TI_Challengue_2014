<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		<title>Panel Control - TrackingNevera</title>
       
        <link rel="shortcut icon" href="/images/favicon.ico">
       
        <!-- CSS Reset -->
		<link rel="stylesheet" type="text/css" href="css/reset.css" media="screen" />
		<link rel="stylesheet" type="text/css" href="css/grid.css" media="screen" />
		
        <!-- IE Hacks for the Fluid 960 Grid System -->
        <!--[if IE 6]><link rel="stylesheet" type="text/css" href="css/ie6.css" media="screen" /><![endif]-->
		    <!--[if IE 7]><link rel="stylesheet" type="text/css" href="css/ie.css" media="screen" /><![endif]-->
        
        <!-- Main stylesheet -->
        <link rel="stylesheet" type="text/css" href="css/styles.css" media="screen" />
        <link rel="stylesheet" type="text/css" href="css/thickbox.css" media="screen" />
        <link rel="stylesheet" type="text/css" href="css/theme-blue.css" media="screen" />
        
		<!-- JQuery engine script-->
		<script type="text/javascript" src="http://code.jquery.com/jquery-1.10.1.min.js"></script>
 		<script type="text/javascript" src="js/jquery.pstrength-min.1.2.js"></script>
		<script type="text/javascript" src="js/thickbox.js"></script>
		
		<script>		
    $(document).ready(
            function() {
                setInterval(function() {

                    $("#menuInfoDashboard").load(<? echo "'$_SERVER[REQUEST_URI]&reload=menuInfoDashboard'"; ?>);
                    $("#menuNeveraDashboardIn").load(<? echo "'$_SERVER[REQUEST_URI]&reload=menuNeveraDashboardIn'"; ?>);
                    $("#menuLogDashboard").load(<? echo "'$_SERVER[REQUEST_URI]&reload=menuLogDashboard'"; ?>);
                }, 5000);
            
                setInterval(function() {
                    $("#menuMapaDashboard").load(<? echo "'$_SERVER[REQUEST_URI]&reload=menuMapaDashboard'"; ?>);
                }, 20000);


            });
            
            function addTrack(){
            	$("#menuInfoDashboard").load(<? echo "'$_SERVER[REQUEST_URI]&reload=menuInfoDashboard&cmd=addTrackRef'"; ?>);
            }
            
            function deleteTrack(){
            	$("#menuInfoDashboard").load(<? echo "'$_SERVER[REQUEST_URI]&reload=menuInfoDashboard&cmd=deleteTrackRef'"; ?>);
            }
            
            function resetTrack(){
            	$("#menuInfoDashboard").load(<? echo "'$_SERVER[REQUEST_URI]&reload=menuInfoDashboard&cmd=resetTrackRef'"; ?>);
            }
            
            function saveMapConfig(){
            	var numCoord = $( "#selectNumCoordenadasMapa" ).val();
            	var refreshco = $( "#selectRefrescoMapa" ).val();
            	var url1 = '<? echo $_SERVER[REQUEST_URI]; ?>' ;
            	var url2 = ''+url1+'&reload=menuMapaDashboard&cmd=saveMapConfig&numeroCoorMapa='+numCoord+'&refrescoMapa='+refreshco+'';
            	           	
            	$("#menuMapaDashboard").load(url2);

            }
    </script>


        
	</head>
	<body>
    	<!-- Header -->
        <div id="header">
            <!-- Header. Status part -->
            <div id="header-status">
                <div class="container_12">
                    <div class="grid_8">
					&nbsp;
                    </div>
                    <div class="grid_4">
                        <a href="funciones.php?func=salir" id="logout">Cerrar sesión</a>
                    </div>
                </div>
                <div style="clear:both;"></div>
            </div> <!-- End #header-status -->
            
            <!-- Header. Main part -->
            <div id="header-main">
                <div class="container_12">
                    <div class="grid_12">
                        <div id="logo">
                            <ul id="nav">
                                <li <? if($zona==""){echo 'id="current"'; } ?>><a href="?">Lista de Dispositivos</a></li>
                                <li <? if($zona=="ajustes"){echo 'id="current"'; } ?>><a href="?zona=ajustes">Configuracion</a></li>
                                <li <? if($zona=="problemas"){echo 'id="current"'; } ?>><a href="?zona=problemas">Problemas Tecnicos</a></li>
                                <li <? if($zona=="sysinfo"){echo 'id="current"'; } ?>><a href="?zona=sysinfo">Sys-Info</a></li>
                            </ul>
                        </div><!-- End. #Logo -->
                    </div><!-- End. .grid_12-->
                    <div style="clear: both;"></div>
                </div><!-- End. .container_12 -->
            </div> <!-- End #header-main -->
            <div style="clear: both;"></div>
            <!-- Sub navigation -->
            <div id="subnav">
                <div class="container_12">
                    <div class="grid_12">

                        
                  </div><!-- End. .grid_12-->
                </div><!-- End. .container_12 -->
                <div style="clear: both;"></div>
            </div> <!-- End #subnav -->
        </div> <!-- End #header -->