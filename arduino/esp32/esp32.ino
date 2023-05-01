#include <ESP32Servo.h>

//Header files for connecting to Firebase
#include <Firebase_ESP_Client.h>
#include "addons/TokenHelper.h"
#include "addons/RTDBHelper.h"

static const int servo1Pin = 13;
static const int servo2Pin = 12;

// Objects required for Firebase connection and authentication
FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;
FirebaseJson jsongps;

// Variables for storing Firebase credentials
#define API_KEY "AIzaSyD1W4mZTisXYJrmmeupRw73CNfbwAnRWG0"
#define DATABASE_URL "https://pro-medic-3db93-default-rtdb.europe-west1.firebasedatabase.app"
#define USER_EMAIL "promedic@gmail.com"
#define USER_PASSWORD "promedic123"
unsigned long sendDataPrevMillis = 0;

// Wifi credentials
const char* ssid = "providence_faculty-2.4G";
const char* password = "PRChostel";

Servo servo1;
Servo servo2;
const int type = 2;

int servo1Angle = 90;
int servo1AngleStep = 10;

int servo1AngleMin = 0;
int servo1AngleMax = 180;

int servo2Angle = 90;
int servo2AngleStep = 10;

int servo2AngleMin = 0;
int servo2AngleMax = 180;

int buttonPushed = 0;

bool flag = true;


void setup() {
  Serial.begin(115200);
  Serial.println("Wait for WiFi... ");
  // Connecting to wifi and setting events
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);
  Serial.println("\n");
  vTaskDelay(1000);
  //Waiting for wifi connection
  while (WiFi.status() != WL_CONNECTED)
    vTaskDelay(500);
  Serial.println("Connected to Wifi");

  // Establishing connection with firebase
  config.api_key = API_KEY;
  auth.user.email = USER_EMAIL;
  auth.user.password = USER_PASSWORD;
  config.database_url = DATABASE_URL;
  Firebase.reconnectWiFi(true);
  fbdo.setResponseSize(4096);
  config.token_status_callback = tokenStatusCallback;
  config.max_token_generation_retry = 5;
  Firebase.begin(&config, &auth);

  servo1.attach(servo1Pin);
  servo2.attach(servo2Pin);
}
void loop() {
  int servonumber = 0;
  if (Firebase.ready() && (millis() - sendDataPrevMillis > 1000 || sendDataPrevMillis == 0) && flag) {
    sendDataPrevMillis = millis();
    if (Firebase.RTDB.getInt(&fbdo, "/servo")) {
      if (fbdo.dataType() == "int") {
        servonumber = fbdo.intData();
      }
    }
    else {
      Serial.println(fbdo.errorReason());
    }
  }
  if (servonumber == 1) {
    flag = false;
    buttonPushed = 1;
  }
  if (servonumber == 2) {
    flag = false;
    buttonPushed = 2;
  }

  if ( buttonPushed == 1) {
    servo1Angle += servo1AngleStep;
    // reverse the direction of the moving at the ends of the angle:
    if (servo1Angle >= servo1AngleMax) {
      servo1AngleStep = -servo1AngleStep;
      if (type == 1) {
        resetServo();
      }
    }

    if (servo1Angle <= servo1AngleMin) {
      servo1AngleStep = -servo1AngleStep;
      if (type == 2)
        resetServo();
    }
    Serial.print("Moving to:");//print on Serial Monitor
    Serial.println(servo1Angle); //print on Serial Monitor

  }//if pushed

  else if ( buttonPushed == 2) {
    servo2Angle += servo2AngleStep;
    // reverse the direction of the moving at the ends of the angle:
    if (servo2Angle >= servo2AngleMax) {
      servo2AngleStep = -servo2AngleStep;
      if (type == 1)
        resetServo();
    }

    if (servo2Angle <= servo2AngleMin) {
      servo2AngleStep = -servo2AngleStep;
      if (type == 2)
        resetServo();
    }
    Serial.print("Moving to:");//print on Serial Monitor
    Serial.println(servo2Angle); //print on Serial Monitor

  }//if pushed

  servo1.write(servo1Angle);
  servo2.write(servo2Angle);
  delay(20);
}

void resetServo()
{
  buttonPushed = 0;
  flag = true;
  if (Firebase.ready())
  {
    if (Firebase.RTDB.setInt(&fbdo, "servo", 0)) {
      Serial.println("Servo 1 Reset");
    }
    else {
      Serial.println("FAILED");
      Serial.println("REASON: " + fbdo.errorReason());
    }
  }
}
