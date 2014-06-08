/*
  Blink
  The basic Energia example.
  Turns on an LED on for one second, then off for one second, repeatedly.
  Change the LED define to blink other LEDs.
  
  Hardware Required:
  * LaunchPad with an LED
  
  This example code is in the public domain.
*/

// most launchpads have a red LED
 // Asignacion de pines
 const int ledRojo  = P2_1; //Indicador de encendido
 const int ledVerde = P2_2; //BT conexion establecida
 const int ledNaranja = P2_3; //Led transmisión BT RX/TX

//see pins_energia.h for more LED definitions
//#define LED GREEN_LED
  
// the setup routine runs once when you press reset:
void setup() {                
  // initialize the digital pin as an output.
  pinMode(ledRojo, OUTPUT);
  pinMode(ledVerde, OUTPUT);
  pinMode(ledNaranja, OUTPUT);
}

// the loop routine runs over and over again forever:
void loop() {
arranqueLuces(100, 3);
delay(3000);
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
