#include <String.h>
#include <SoftwareSerial.h>
#include <Wire.h>

#define RxD P2_2
#define TxD P2_3

#define ADXL345ADDRESS 0x53   //Dirección del acelerómetro
#define DATAADDRESS 0x32      //Primer registro con los datos de los ejes
#define POWER_CTL 0x2D        //Registro de control de energía
#define POWER_CTL_VALUE 8     //Valor que cargaremos en POWER_CTL
#define LENGTH 6   
#define SCALE2G 0.0039

#define ledEncendido P2_1 //Indicador de encendido
#define ledTx P1_5 //Led transmisión BT RX/TX
#define ledRx P2_0 //BT conexion establecida
#define pinRst P1_3 //Pin encedido bluetooth
#define pinKey P2_4 //Pin para entrar en modo programacion del bluetooth
#define modoProgramacion 0 //Entramos en modo programacion o no

 SoftwareSerial BTSerial(RxD, TxD);
 const int MUESTRAS = 256;
 float tempGlobal = 0;

 int sigoVivo = 0;

 //Variables globales del acelerometro
 uint8_t* p_buffer = NULL;
 int xAxis=0;    //Variables que usaremos para imprimir el resultado
 int yAxis=0;
 int zAxis=0;
 
 float scaledXAxis = 0;    //Variables que usaremos para imprimir el resultado con una escala mas intuitiva
 float scaledYAxis = 0;
 float scaledZAxis = 0;


void setup()
{
 
   // initialize the digital pin as an output.
  pinMode(ledEncendido, OUTPUT);
  pinMode(ledTx, OUTPUT);
  pinMode(ledRx, OUTPUT);
  pinMode(pinRst, OUTPUT);
  pinMode(pinKey, OUTPUT);

  
  // Estado inicial
  arranqueLuces(70, 3); //hacemos test de luces
  delay(300);
  digitalWrite(ledEncendido, HIGH);   
  
  if(modoProgramacion == 0){
    // Configuracion del puerto serie por software
    // para comunicar con el modulo HC-05 
    digitalWrite(pinKey, LOW); // Modo comunicación bluetooth
    delay(100); // Nos aseguramos de que pille que el pin está apagado
    digitalWrite(pinRst, HIGH); //Encendemos el modulo bluetooth
    delay(500); //Espereamos un poco a que se caliente el chip
    BTSerial.begin(57600); //Baudios configurados 19200
    BTSerial.flush();
    delay(500);
    //BTSerial.println("Conexión Establecida");
  }else{
    //NECESITA EL PIN KEY EN HIGH y un baudratio de 38400 para que funcione
    digitalWrite(pinKey, HIGH); // Modo configuración bluetooth
    delay(100); //Nos aseguramos de que pille que el pin está encendido
    digitalWrite(pinRst, HIGH); //Encendemos el modulo bluetooth
    BTSerial.begin(38400); //Baudios necesarios para el modo programacion
    BTSerial.flush();
    delay(1000);
    //BTSerial.print("at+name=75595RF\r\n"); //Comando para cambiar el nombre del enlace
    delay(600);
    //BTSerial.print("AT+UART=57600,0,0\r\n"); //Comando para ajustar los baudios
  }
  
  //Configurar el ADC para que use la referencia interna de 1.5V
  analogReference(INTERNAL1V5);
  delay(1000); // Esperamos un poco
  
  //Configuramos el acelerometro
  Wire.begin();  //Iniciamos el bus I2C en modo maestro
  writeTo(ADXL345ADDRESS,POWER_CTL, POWER_CTL_VALUE);  //Antes de poder realizar mediciones se necesita configurar el registro de control de energía
}

//Reinicio por software
void(* resetFunc) (void) = 0; //declare reset function @ address 0

void loop()
{
 
  //Si nos pasamos de veces sin que el movil contacte con el micro reiniciamos la conexión
  if (sigoVivo >= 20){
  BTSerial.println("Reiniciando conexion :(");
  digitalWrite(ledTx, HIGH);
  digitalWrite(ledRx, LOW); //Apagamos el led que nos indica que la conexión está viva
  digitalWrite(pinRst, LOW); //Apagamos el modulo bluetooth
  delay(5000); //Esperamos un ratito para ver que se ha pagado el led y reiniciamos
  resetFunc();  //reiniciamos el chip
  }
  
  // Esperamos ha recibir datos.
  if (BTSerial.available()){
    
    // La funcion read() devuelve un caracter 
    char command = BTSerial.read();
    BTSerial.flush();
    
    // Que hacer cuando recibe información
   
    //Si recivimos el caracter a mantemeos la conexión viva
    //reiniciar la cuenta atras de sigoVivo
     if (command == 'a'){
      BTSerial.println("Alive packet");
      digitalWrite(ledRx, HIGH);
      sigoVivo = 0;
    }
    
    //Si recibimos t de test, hacemos un parpadeo de prueba
    if (command == 't'){
      BTSerial.println("Parpadeo TX led || Haciendo test");
      parpadeo(60,5,ledTx); 
    }
    
  }
  
  //Leemos la temperatura y la transmistimos por blueetooth
  sigoVivo++;
  leerTemperatura();
  delay(200);
  medirPosicion();
  delay(200);
  //Enviamos los resultados
   BTSerial.print("temp: ");
   BTSerial.print(tempGlobal);
   BTSerial.print(" |x-axis: "); //temperatura + x-axis + y-axis + z-axis
   BTSerial.print(scaledXAxis);
   BTSerial.print(" |y-axis: ");
   BTSerial.print(scaledYAxis);
   BTSerial.print(" |z-axis: ");
   BTSerial.print(scaledZAxis);
   BTSerial.print("\r\n");
  //Esperamos un rato para no gaste mucha batería
   delay(3500);  
}


/****************************** FUNCIONES Generales *******************************/

 void leerTemperatura(){
  
   long int temp = 0;       // almacenamos el valor leido de la temperatura
   long int tempMedia = 0;  //ya que el termómetro es un dispositivo que genera una salida con mucho ruido                     
   long int numMuestras = 0; 
   float final = 0;
   
   for(numMuestras = 0; numMuestras < MUESTRAS; numMuestras++){
    //TEMPSENSOR es un pin interno el cual está conectado al termómetro
    temp = ((uint32_t)analogRead(A10)*27069 - 18169625) * 10 >> 16; //escalamos el valor que devuelve el ADC
    tempMedia += temp; //sumamos cada muestra
   }                                          
  
  tempMedia /= MUESTRAS; //dividimos por el número de muestras 
  tempMedia = tempMedia - 150; //Ajustamos el offset(desviación de cada micro)  
  tempGlobal = tempMedia * 0.10;
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

/****************************** FUNCIONES ACELROMETRO *******************************/

void medirPosicion(){
 p_buffer = readFrom(ADXL345ADDRESS, DATAADDRESS, LENGTH); //Lectura de los datos del acelerómetro

 xAxis = (p_buffer[1] << 8) | p_buffer[0]; //El acelerómetro almacena el valor de cada eje en dos bytes por lo que 
 yAxis = (p_buffer[3] << 8) | p_buffer[2]; //necesitamos hacer esto para tener el dato completo
 zAxis = (p_buffer[5] << 8) | p_buffer[4];
 
 scaledXAxis = SCALE2G * xAxis; //Escalamos el valor en "G"
 scaledYAxis = SCALE2G * yAxis;
 scaledZAxis = SCALE2G * zAxis;
 
}

void writeTo(int slave_Address, int address, int data){
  
	Wire.beginTransmission(slave_Address); //Ponemos en el bus la dirección del dispositivo con el que nos quereos comunicar
	Wire.write(address);                   //Registro al que queremos acceder
	Wire.write(data);                      //Dato que queremos poner en el registro
	Wire.endTransmission();                //Terminar la transmisión
}



uint8_t* readFrom(int slave_Address, int address, int length){
  
  uint8_t buffer[length];                   //Buffer donde iremos guardando los datos que recibamos
  
  Wire.beginTransmission(slave_Address);    //Dirección del dispositivo con el que nos queremos comunicar
  Wire.write(address);                      //Que registro al que queremos acceder
  Wire.endTransmission();                 
  
  Wire.beginTransmission(slave_Address);    //Start repetido
  Wire.requestFrom(slave_Address, length);  //Modo lectura y cuantos bytes queremos
  
  if(Wire.available() == length){         
    for(uint8_t i = 0; i < length; i++){    //Leemos cada byte y rellenamos el buffer
      buffer[i] = Wire.read();
    }
  }    
  Wire.endTransmission();                 

  return buffer;
}
