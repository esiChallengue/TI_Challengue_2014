/*
  Tutorial 9 - Ejercicio Propuesto 1
  Uso de las interrupciones del acelerómetro
 
  Taller de uControladores ESIBot 2014
 */
 

// Incluimos la libreria Wire.h la cual implementa el protocolo I2C
#include <Wire.h>
#include "acelerometro2.h"

#define INTERRUPTPIN P2_3
#define ROJO P1_3
#define VERDE P2_1
#define AZUL P2_2

void setup()
{
  
  Serial.begin(9600); 
  Wire.begin();          //Iniciamos el bus I2C en modo maestro

  pinMode(AZUL, OUTPUT);
  pinMode(VERDE, OUTPUT);
  pinMode(ROJO, OUTPUT);
  
  digitalWrite(ROJO, LOW);
  digitalWrite(VERDE, LOW);
  digitalWrite(AZUL, LOW);

  pinMode(INTERRUPTPIN, INPUT);


  //ConfiguraciÃ³n de los tres registros que itnervienen en la detecciÃ³n de golpes
  write(ADXL345ADDRESS, DUR, 0x1F  ); // 625us/LSB
  write(ADXL345ADDRESS, THRESH_TAP, 48); // 62.5mg/LSB  <==> 3000mg/62.5mg = 48 LSB as datasheet suggestion
  write(ADXL345ADDRESS, TAP_AXES, 1); //Seleccionamos que la interrupcion se produzca con el valor de x
  
  //ConfiguraciÃ³n de los registros referentes a interrupciones
  write(ADXL345ADDRESS, INT_MAP, 0b10111111); //Enviamos todas las interrupciones al pin INT1. Antes estaba a cero, enviaria todas las interrupciones por INT1
  write(ADXL345ADDRESS, INT_ENABLE, 0b01000000); //Activamos la interrupciÃ³n por detecciÃ³n de golpe simple

  //ConfiguraciÃ³n del control de energÃ­a
  write(ADXL345ADDRESS,POWER_CTL, POWER_CTL_VALUE); 
 
}

int contador = 0; //Va contando el numero de golpes
int color = 0;

char aux=0; 
 
uint8_t* p_buffer = NULL;

float xAxis;    //Variables que usaremos para imprimir el resultado con una escala mas intuitiva
float yAxis;
float zAxis;




void loop(){
      digitalWrite(ROJO, LOW);
      digitalWrite(VERDE, LOW);
      digitalWrite(AZUL, LOW); 
  Serial.println("Comienzo programa");
  
  while (digitalRead(INTERRUPTPIN) == 0){
    
    p_buffer = read(ADXL345ADDRESS, DATAADDRESS, LENGTH); //Lectura de los datos del acelerometro
    
    xAxis = SCALE2G * ((p_buffer[1] << 8) | p_buffer[0]); //El acelerÃ³metro almacena el valor de cada eje en dos bytes por lo que 
    yAxis = SCALE2G * ((p_buffer[3] << 8) | p_buffer[2]); //necesitamos hacer esto para tener el dato completo
    zAxis = SCALE2G * ((p_buffer[5] << 8) | p_buffer[4]);
  
    Serial.print("Aceleration:\t"); //Imprimimos los valores obtenidos
    Serial.print(xAxis);
    Serial.print("   ");   
    Serial.print(yAxis);
    Serial.print("   ");   
    Serial.println(zAxis);
    
    //Serial.print("Interruption value:");
    Serial.println(digitalRead(INTERRUPTPIN));
 
    delay(500);
  }
  
   Serial.println("Se ha producido una interruption");
   Serial.println("*********");
   Serial.print("Numero de interrupcion: "); 
   Serial.println(contador);
   
    read(ADXL345ADDRESS, INT_SOURCE,48);
    read(ADXL345ADDRESS, ACT_TAP_STATUS, 1);
    
      contador++;
      color = contador%3;
     
     // Líneas usadas para la depuración. 
     // Serial.print("Valor de color");
     // Serial.println(color);
     
      if (color == 0){
        Serial.println("VERDE");
        digitalWrite(VERDE, HIGH); 
        delay(300);
      }else if (color == 1){
        Serial.println("ROJO");
        digitalWrite(ROJO, HIGH);
        delay(300);
      }else{
        Serial.println("AZUL");
        digitalWrite(AZUL, HIGH);
        delay(300);
      }
   
   write(ADXL345ADDRESS, INT_ENABLE, 0b01000000); //Desactivamos el bit de enable para que no se enlace dos interrupciones y que cuadno empiecen sea como un reset.

 }

/****************************** FUNCIONES *******************************/

void write(int slave_Address, int address, int data){
  
	Wire.beginTransmission(slave_Address); //Ponemos en el bus la direcciÃ³n del dispositivo con el que nos quereos comunicar
	Wire.write(address);                   //Registro al que queremos acceder
	Wire.write(data);                      //Dato que queremos poner en el registro
	Wire.endTransmission();                //Terminar la transmisiÃ³n
}



uint8_t* read(int slave_Address, int address, int length){
  
  uint8_t buffer[length];                   //Buffer donde iremos guardando los datos que recibamos
  
  Wire.beginTransmission(slave_Address);    //DirecciÃ³n del dispositivo con el que nos queremos comunicar
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





