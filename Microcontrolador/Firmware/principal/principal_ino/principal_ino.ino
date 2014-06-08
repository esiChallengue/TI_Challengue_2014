#include <SoftwareSerial.h>

#define RxD P2_2
#define TxD P2_3

const int ledEncendido  = P2_1; //Indicador de encendido
const int ledTx = P1_5; //BT conexion establecida
const int ledRx = P2_0; //Led transmisión BT RX/TX

SoftwareSerial BTSerial(RxD, TxD);
byte pinEstado = 0;
const int MUESTRAS = 256;

void setup()
{
  
   // initialize the digital pin as an output.
  pinMode(ledEncendido, OUTPUT);
  pinMode(ledTx, OUTPUT);
  pinMode(ledRx, OUTPUT);
  
  // Estado inicial
  arranqueLuces(70, 3);
  delay(500);
  digitalWrite(ledEncendido, HIGH);
   
  
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
      toggle(ledRx); 
    }
    
     if (command == 'b'){
      BTSerial.println("Parpadeo RX led");
      parpadeo(60,5,ledRx); 
    }
    
    if (command == 'c'){
      BTSerial.println("Parpadeo TX led");
      parpadeo(60,5,ledTx); 
    }
    
  }
  
  //Leemos la temperatura y la transmistimos por blueetooth
  leerTemperatura();
  //Esperamos un rato para no gasta rmucha batería
   delay(5000);  
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
  parpadeo(60,5,ledTx);
  
}


void toggle(int pinNum) {
  // Establece el pin del LED usando la variable pinEstado:
  digitalWrite(pinNum, pinEstado); 
  // si el pinEstado = 0, lo establece a 1, y vice versa:
  pinEstado = !pinEstado;
}

int parpadeo(int freq, int veces, int pinNum){
  for(int i=0;i<veces;i++){
  digitalWrite(pinNum, HIGH);
  delay(freq);
  digitalWrite(pinNum, LOW);
  }
}

int arranqueLuces(int freq, int veces){
  for(int i=0;i<veces;i++){
  //1 Pasada de izquierda a derecha
  digitalWrite(ledEncendido, HIGH);
  delay(freq);
  digitalWrite(ledEncendido, LOW);
  digitalWrite(ledTx, HIGH);
  delay(freq);
  digitalWrite(ledTx, LOW);
  digitalWrite(ledRx, HIGH);
  delay(freq);
  digitalWrite(ledRx, LOW);
  delay(freq/2);
  //1 pasada de derecha a izquierda
  digitalWrite(ledRx, HIGH);
  delay(freq);
  digitalWrite(ledRx, LOW);
  digitalWrite(ledTx, HIGH);
  delay(freq);
  digitalWrite(ledTx, LOW);
  digitalWrite(ledEncendido, HIGH);
  delay(freq);
  digitalWrite(ledEncendido, LOW); 
  delay(freq/2); 
  }
}
