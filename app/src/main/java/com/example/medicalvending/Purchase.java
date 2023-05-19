package com.example.medicalvending;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Purchase extends AppCompatActivity implements PaymentResultListener {

    private TextView name, price;
    private Button payBtn, logoutButton;
    private DatabaseReference mDatabase;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        payBtn = findViewById(R.id.idBtnPay);
        logoutButton = findViewById(R.id.logout);

        intent = getIntent();
        mDatabase = FirebaseDatabase.getInstance("https://pro-medic-3db93-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        name.setText("Selected Medicine : " + intent.getStringExtra("name"));
        price.setText("Total amount to be paid = RS " + intent.getStringExtra("price"));
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }

        });


        // adding on click listener to our button.
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String samount = intent.getStringExtra("price");

                // rounding off the amount.
                int amount = Math.round(Float.parseFloat(samount) * 100);

                // initialize Razorpay account.
                Checkout checkout = new Checkout();

                // set your id as below
                checkout.setKeyID("rzp_test_kygXqIdPnN1DxZ");


                // initialize json object
                JSONObject object = new JSONObject();
                try {
                    // to put name
                    object.put("name", "PRO MEDIC");

                    // put description
                    object.put("description", "Payment");

                    // to set theme color
                    object.put("theme.color", "#25383C");

                    // put the currency
                    object.put("currency", "INR");

                    // put amount
                    object.put("amount", amount);


                    // put email
//                    object.put("prefill.email", "examhertz@gmail.com");

                    // open razorpay to checkout activity
                    checkout.open(Purchase.this, object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    public void onPaymentSuccess(String s){
        Toast.makeText(Purchase.this, "Payment is successful : " + s, Toast.LENGTH_SHORT).show();
        if(Objects.equals(intent.getStringExtra("name"), "Paracetamol"))
            mDatabase.child("servo").setValue(1);
        else
            mDatabase.child("servo").setValue(2);
        Map<String, Object> updates = new HashMap<>();
        updates.put("stock/"+intent.getStringExtra("name"), ServerValue.increment(-1));
        mDatabase.updateChildren(updates);
        Intent intent = new Intent(getApplicationContext(), Sucessful.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onPaymentError(int i, String s){
        Toast.makeText(Purchase.this, "Payment failed due to error : " + s, Toast.LENGTH_SHORT).show();
    }

}