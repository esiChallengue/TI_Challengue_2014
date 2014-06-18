#include <SoftwareSerial.h>

#define RxD P2_2
#define TxD P2_3

const int ledEncendido  = P2_1; //Indicador de encendido
const int ledTx = P1_5; //Led transmisión BT RX/TX
const int ledRx = P2_0; //BT conexion establecida
const int pinRst= P1_3; //Pin encedido bluetooth
const int pinKey= P2_4; //Pin para entrar en modo programacion del bluetooth

SoftwareSerial BTSerial(RxD, TxD);
const int MUESTRAS = 256;

int sigoVivo = 0;
const int modoProgramacion = 0;

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
    BTSerial.begin(19200); //Baudios configurados 19200
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
    BTSerial.print("at+name=65878RA\r\n"); //Comando para cambiar el nombre del enlace
    delay(600);
    //BTSerial.print("AT+UART=19200,0,0\r\n"); //Comando para ajustar los baudios
  }
  
  //Configurar el ADC para que use la referencia interna de 1.5V
  analogReference(INTERNAL1V5);
  delay(1000); // Esperamos un poco
}

//Reinicio por software
void(* resetFunc) (void) = 0; //declare reset function @ address 0

void loop()
{
 
  //Si nos pasamos de veces sin que el movil contacte con el micro reiniciamos la conexión
  if (sigoVivo >= 12){
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
  leerTemperatura();
  sigoVivo++;
  //Esperamos un rato para no gaste mucha batería
   delay(5000);  
}


int leerTemperatura(){
  
   long int temp = 0;       // almacenamos el valor leido de la temperatura
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
  tempMedia = tempMedia - 150; //Ajustamos el offset(desviación de cada micro)
  
  BTSerial.println("Temp: "+ String(tempMedia)); //
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
