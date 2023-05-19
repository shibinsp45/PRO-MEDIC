#include <ESP32Servo.h>

//Header files for connecting to Firebase
#include <Firebase_ESP_Client.h>
#include "addons/TokenHelper.h"
#include "addons/RTDBHelper.h"

// Objects required for Firebase connection and authentication
FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;

// Variables for storing Firebase credentials
#define API_KEY "AIzaSyD1W4mZTisXYJrmmeupRw73CNfbwAnRWG0"
#define DATABASE_URL "https://pro-medic-3db93-default-rtdb.europe-west1.firebasedatabase.app"
#define USER_EMAIL "promedic@gmail.com"
#define USER_PASSWORD "promedic123"
unsigned long sendDataPrevMillis = 0;

// Wifi credentials
const char* ssid = "Cannot connect to this network";
const char* password = "qwerty123";

static const int servo1Pin = 13;
static const int servo2Pin = 12;
Servo servo1;
Servo servo2;
int servoNumber = 0;

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
    delay(500);
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

  servo1.write(0);
  servo2.write(90);
}

void loop() {
  if (Firebase.ready() && (millis() - sendDataPrevMillis > 2000 || sendDataPrevMillis == 0)) {
    sendDataPrevMillis = millis();
    if (Firebase.RTDB.getInt(&fbdo, "/servo")) {
      if (fbdo.dataType() == "int") {
        servoNumber = fbdo.intData();
      }
    } else {
      Serial.println(fbdo.errorReason());
    }
    if (servoNumber == 1) {
      servo1.write(0);
      delay(500);
      servo1.write(90);
      delay(500);
      servo1.write(0);
      delay(500);
    } else if (servoNumber == 2) {
      servo2.write(90);
      delay(500);
      servo2.write(0);
      delay(500);
      servo2.write(90);
      delay(500);
    }
    resetServo();
  }
}

void resetServo() {
  if (Firebase.ready()) {
    if (Firebase.RTDB.setInt(&fbdo, "servo", 0)) {
      Serial.println("Servo Reset");
      servo1.write(0);
      servo2.write(90);
      servoNumber = 0;
    } else {
      Serial.println("FAILED");
      Serial.println("REASON: " + fbdo.errorReason());
    }
  }
}
