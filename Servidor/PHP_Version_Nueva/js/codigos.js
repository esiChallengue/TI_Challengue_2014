/**** Inicio  Funciones de Recortar fotos **/
var _IE_ = navigator.userAgent.indexOf("MSIE") != -1; // Si es IE
	var inicio = false;
	var xini = 0;
	var yini = 0;
	var xfin = 0;
	var yfin = 0;
	var id_imagen = 0 ;
	
	function posicionaMarco(e) {
		inicio = !inicio;
		var marco = document.getElementById("marco");
		
		if (inicio) {
			marco.style.display = "block";

			// En IE y Opera se usa otra propiedad del evento
			if (_IE_) {
				xini = e.offsetX;
				yini = e.offsetY;
			} else {
				xini = e.layerX;
				yini = e.layerY;
			}
			marco.style.left = xini+"px";
			marco.style.top = yini+"px";
			marco.style.width = "0px";
			marco.style.height = "0px";
		} 
	}
	
	function despliegaMarco(e) {

		if (inicio) {
			var marco = document.getElementById("marco");

			// En IE y Opera se usa otra propiedad del evento
			if (_IE_) {
				xfin = e.offsetX-7;
				yfin = e.offsetY-7;
			} else {
				xfin = e.layerX-7;
				yfin = e.layerY-7;
			}
			if (xfin > xini+5) {
				marco.style.width = (xfin-xini)+"px";
			}
			if (yfin > yini+5) {
				marco.style.height = (yfin-yini)+"px";
			}
		}
	}


	function cortar() {
	img = document.getElementById("imagen_cortada");
      var prueboRequest = new Request({ method: 'get', 
         url: "funciones.php?func=recortar&xini="+(xini)+"&yini="+(yini)+"&xfin="+(xfin)+"&yfin="+(yfin),
         onRequest: function() { alert("Se está generando la imagen cortada, en aproximadamente 10 segundos se le mostrará abajo.");}, 
         onSuccess: function(){img.src="../actualidad/noticias/imagenes/temporal_del_cortado.jpg?rand=" + Math.random();},
         onFailure: function(){alert('Fallo');}
      }).send();  
	}

	function cortar2() {
	img = document.getElementById("imagen_cortada");
      var prueboRequest = new Request({ method: 'get', 
         url: "funciones.php?func=recortar2&xini="+(xini)+"&yini="+(yini)+"&xfin="+(xfin)+"&yfin="+(yfin)+"&id_imagen="+(id_imagen),
         onRequest: function() { alert("Se está generando la imagen cortada, en aproximadamente 10 segundos se le mostrará abajo.");}, 
         onSuccess: function(){img.src="../actualidad/noticias/imagenes/temporal_del_cortado.jpg?rand=" + Math.random();},
         onFailure: function(){alert('Fallo');}
      }).send();  
	}

	function finalizar() {
   document.location.href='administracion.php?zona=noticias';
	}
/********  Fin funciones recortar foto ******/

/************* Inicio Cargar AJAX **********/

/************* Fin cargar AJAX ************/