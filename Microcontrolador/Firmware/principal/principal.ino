 //INCLUSIÖN de librerías
 #include <SoftwareSerial.h>
 
 // Asignacion de pines
 const int ledRojo  = P2_1; //Indicador de encendido
 const int ledVerde = P2_2; //BT conexion establecida
 const int ledNaranja = P2_3; //Led transmisión BT RX/TX
 const int pinTX = P1_0; //Pin Transmisión datos TX (DEJAMOS EL PIN UART TX para debug)
 const int pinRX = P1_3; //PIN Recepción datos RX (DEJAMOS EL PIN UART RX para debug)
 const int pinRST = P2_4; //PIN encendido modulo bluetooth
 const int pinKEY = P2_5; //PIN modificación modulo bluetooth AT COMMANDS
 ///////////////////////////////////////////
 int flagArranque = 0;
 
 SoftwareSerial BTSerial(pinRX, pinTX);
 const int MUESTRAS = 256;

void setup() {
  // Configuración de PINES
  pinMode(ledRojo, OUTPUT);
  pinMode(ledVerde, OUTPUT);
  pinMode(ledNaranja, OUTPUT);
  pinMode(pinRST, OUTPUT);
  pinMode(pinKEY, OUTPUT);
  
  // Estado inicial
  digitalWrite(pinRST, LOW);
  // Modo Comunicacion
  digitalWrite(pinKEY, LOW); 
   
  // Encendemos el modulo.
  digitalWrite(pinRST, HIGH);
  
  // Configuracion del puerto serie por software
  // para comunicar con el modulo HC-05/HC-06
  BTSerial.begin(9600);
  BTSerial.println("Conectado");
  BTSerial.flush();
  delay(500);
  
  // Configuramos el puerto serie de Arduino para Debugge por serial monitor
  Serial.begin(9600);
  Serial.println("Ready");
  
  //Configurar el ADC para que use la referencia interna de 1.5V
  analogReference(INTERNAL1V5);
  
}

void loop() {
  
  if(flagArranque == 0){
   arranqueLuces(100, 3);
   flagArranque = 1;
   BTSerial.println("Parpadeo luces");
  }
  
  //Leemos la temperatura y la transmistimos por blueetooth
  leerTemperatura();
  
  //Esperamos 7 segundos, para mandar la siguiente temperatura
  delay(7000);
}


//////////////////////////////////////////////
//      DEFINICION DE FUNCIONES            //
////////////////////////////////////////////

int leerTemperatura(){
  
   long int temp = 0;       //almacenamos el valor leido de la temperatura
   long int tempMedia = 0;  //ya que el termómetro es un dispositivo que genera una salida con mucho ruido
                         //lo leeremos varias veces y calcularemos la media para tener una medida mas precisa
   long int numMuestras = 0; 
   
   for(numMuestras = 0; numMuestras < MUESTRAS; numMuestras++){
    //TEMPSENSOR es un pin interno el cual está conectado al termómetro
    temp = ((uint32_t)analogRead(A10)*27069 - 18169625) * 10 >> 16; //escalamos el valor que devuelve el ADC
    tempMedia += temp; //sumamos cada muestra
   }                                          
  
  tempMedia /= MUESTRAS; //dividimos por el número de muestras 
 
  BTSerial.println("Temp:"+tempMedia); //
  
}


int arranqueLuces(int freq, int veces){
  for(int i=0;i<veces;i++){
  //1 Pasada de izquierda a derecha
  digitalWrite(ledVerde, HIGH);
  delay(freq);
  digitalWrite(ledVerde, LOW);
  digitalWrite(ledRojo, HIGH);
  delay(freq);
  digitalWrite(ledRojo, LOW);
  digitalWrite(ledNaranja, HIGH);
  delay(freq);
  digitalWrite(ledNaranja, LOW);
  delay(freq/2);
  //1 pasada de derecha a izquierda
  digitalWrite(ledNaranja, HIGH);
  delay(freq);
  digitalWrite(ledNaranja, LOW);
  digitalWrite(ledRojo, HIGH);
  delay(freq);
  digitalWrite(ledRojo, LOW);
  digitalWrite(ledVerde, HIGH);
  delay(freq);
  digitalWrite(ledVerde, LOW); 
  delay(freq/2); 
  }
}
