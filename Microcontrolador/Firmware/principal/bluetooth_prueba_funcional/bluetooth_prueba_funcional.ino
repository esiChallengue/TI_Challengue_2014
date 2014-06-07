/*
 *  Author: Jose Antonio Luceño Castilla
 *  Date  : Septempber 2013
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
#include <SoftwareSerial.h>

#define RxD P2_2
#define TxD P2_3

#define LED P1_0

SoftwareSerial BTSerial(RxD, TxD);
byte pinEstado = 0;
const int MUESTRAS = 256;

void setup()
{
  
  pinMode(LED, OUTPUT);
  
  // Estado inicial
  digitalWrite(LED, HIGH);
   
  
  // Configuracion del puerto serie por software
  // para comunicar con el modulo HC-05
  BTSerial.begin(9600);
  BTSerial.flush();
  delay(500);
  
  BTSerial.println("Conexión Establecida");
    //Configurar el ADC para que use la referencia interna de 1.5V
  analogReference(INTERNAL1V5);

}

void loop()
{

  // Esperamos ha recibir datos.
  if (BTSerial.available()){
    
    // La funcion read() devuelve un caracter 
    char command = BTSerial.read();
    BTSerial.flush();
    
    // En caso de que el caracter recibido sea "L" cambiamos
    // El estado del LED
    if (command == 'a'){
      BTSerial.println("Toggle LED");
      toggle(LED); 
    }
    
  }
  
  //Leemos la temperatura y la transmistimos por blueetooth
  leerTemperatura();
  //Esperamos un rato para no gasta rmucha batería
   delay(7000);  
}


int leerTemperatura(){
  
   long int temp = 0;       //almacenamos el valor leido de la temperatura
   long int tempMedia = 0;  //ya que el termómetro es un dispositivo que genera una salida con mucho ruido
                         //lo leeremos varias veces y calcularemos la media para tener una medida mas precisa
   long int numMuestras = 0; 
   float tempFinal;
   
   for(numMuestras = 0; numMuestras < MUESTRAS; numMuestras++){
    //TEMPSENSOR es un pin interno el cual está conectado al termómetro
    temp = ((uint32_t)analogRead(A10)*27069 - 18169625) * 10 >> 16; //escalamos el valor que devuelve el ADC
    tempMedia += temp; //sumamos cada muestra
   }                                          
  
  tempMedia /= MUESTRAS; //dividimos por el número de muestras 
  
  BTSerial.println("Temp: "+ String(tempMedia)); //
  
}


void toggle(int pinNum) {
  // Establece el pin del LED usando la variable pinEstado:
  digitalWrite(pinNum, pinEstado); 
  // si el pinEstado = 0, lo establece a 1, y vice versa:
  pinEstado = !pinEstado;
}
