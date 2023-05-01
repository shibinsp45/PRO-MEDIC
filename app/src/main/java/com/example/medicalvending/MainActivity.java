package com.example.medicalvending;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Button logoutButton, purchase;
    private CheckBox paracetamol, aspirin;
    private TextView amount, paracetamolStockView, aspirinStockView;
    private DatabaseReference mDatabase;
    private long paracetamolStock, aspirinStock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        logoutButton = findViewById(R.id.logout);
        paracetamol = findViewById(R.id.paracetamol);
        aspirin = findViewById(R.id.aspirin);
        amount = findViewById(R.id.amount);
        purchase = findViewById(R.id.purchase);
        paracetamolStockView = findViewById(R.id.paracetamolstock);
        aspirinStockView = findViewById(R.id.aspirinstock);

        DatabaseReference myRef = FirebaseDatabase.getInstance("https://pro-medic-3db93-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("stock");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                paracetamolStock = (long) dataSnapshot.child("Paracetamol").getValue();
                aspirinStock = (long) dataSnapshot.child("Aspirin").getValue();
                paracetamolStockView.setText("Paracetamol = " + paracetamolStock);
                aspirinStockView.setText("Aspirin = " + aspirinStock);
                paracetamolStockView.setVisibility(View.VISIBLE);
                aspirinStockView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Exception FB",databaseError.toException());
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
                }

        });

        paracetamol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aspirin.setChecked(false);
                amount.setVisibility(View.VISIBLE);
                amount.setText("Total amount = " + "Rs 2");
                if(!paracetamol.isChecked() && !aspirin.isChecked())
                    amount.setVisibility(View.GONE);
            }
        });

        aspirin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paracetamol.setChecked(false);
                amount.setVisibility(View.VISIBLE);
                amount.setText("Total amount = " + "Rs 5");
                if(!paracetamol.isChecked() && !aspirin.isChecked())
                    amount.setVisibility(View.GONE);
            }
        });

        purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!paracetamol.isChecked() && !aspirin.isChecked()) {
                    Toast.makeText(MainActivity.this, "Select atleast one option",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), Purchase.class);
                if(paracetamol.isChecked()){
                    if(paracetamolStock <= 0){
                        Toast.makeText(MainActivity.this, "Not available",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    intent.putExtra("name", "Paracetamol");
                    intent.putExtra("price", "2");
                }
                else{
                    if(aspirinStock <= 0){
                        Toast.makeText(MainActivity.this, "Not available",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    intent.putExtra("name", "Aspirin");
                    intent.putExtra("price", "5");
                }
                startActivity(intent);
            }
        });
    }
}
