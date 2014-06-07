#include <SoftwareSerial.h>

#define RxD P2_2
#define TxD P2_3

SoftwareSerial BTSerial(RxD, TxD);

void setup()
{
  

  
  delay(500);
  
  BTSerial.flush();
  delay(500);
  BTSerial.begin(9600);
  Serial.begin(9600);
  Serial.println("Enter AT commands:");

  BTSerial.print("AT\r\n");
  delay(100);

}

void loop()
{

  if (BTSerial.available())
    Serial.write(BTSerial.read());

  if (Serial.available())
    BTSerial.write(Serial.read());

}
