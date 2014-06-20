/*
  Tutorial 9 - Ejemplo 1
  Comunicación con ADXL345 mediante I2C
 
  Taller de uControladores ESIBot 2014
*/

#include <Wire.h>

#define ADXL345ADDRESS 0x53   //Dirección del acelerómetro

#define DATAADDRESS 0x32      //Primer registro con los datos de los ejes
#define POWER_CTL 0x2D        //Registro de control de energía
#define POWER_CTL_VALUE 8     //Valor que cargaremos en POWER_CTL

#define SCALE2G 0.0039
#define LENGTH 6              

void setup()
{
  Serial.begin(9600); 
  Wire.begin();          //Iniciamos el bus I2C en modo maestro
  pinMode(P2_1, OUTPUT);
  digitalWrite(P2_1, HIGH);

  writeTo(ADXL345ADDRESS,POWER_CTL, POWER_CTL_VALUE);  //Antes de poder realizar mediciones se necesita configurar el registro de control de energía
}

uint8_t* p_buffer = NULL;

int xAxis;    //Variables que usaremos para imprimir el resultado
int yAxis;
int zAxis;

float scaledXAxis;    //Variables que usaremos para imprimir el resultado con una escala mas intuitiva
float scaledYAxis;
float scaledZAxis;


void loop(){
 p_buffer = readFrom(ADXL345ADDRESS, DATAADDRESS, LENGTH); //Lectura de los datos del acelerómetro

 xAxis = (p_buffer[1] << 8) | p_buffer[0]; //El acelerómetro almacena el valor de cada eje en dos bytes por lo que 
 yAxis = (p_buffer[3] << 8) | p_buffer[2]; //necesitamos hacer esto para tener el dato completo
 zAxis = (p_buffer[5] << 8) | p_buffer[4];
 
 scaledXAxis = SCALE2G * xAxis; //Escalamos el valor en "G"
 scaledYAxis = SCALE2G * yAxis;
 scaledZAxis = SCALE2G * zAxis;
 
 Serial.print("Raw:\t"); //Imprimimos los valores obtenidos
 Serial.print(xAxis);
 Serial.print("   ");   
 Serial.print(yAxis);
 Serial.print("   ");   
 Serial.print(zAxis);
 
 Serial.print("   \tScaled:\t");
 Serial.print(scaledXAxis);
 Serial.print("G   ");   
 Serial.print(scaledYAxis);
 Serial.print("G   ");   
 Serial.print(scaledZAxis);
 Serial.println("G");

 delay(200);
}

/****************************** FUNCIONES *******************************/

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

