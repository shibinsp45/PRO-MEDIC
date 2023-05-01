package com.example.medicalvending;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class GetStarted extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        Thread thread = new Thread() {
            public void run(){
                try {
                    sleep( 3000);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                finally{
                    Log.d("hi", "run: ");
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    try{
                        join();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

        };thread.start();
    }
}