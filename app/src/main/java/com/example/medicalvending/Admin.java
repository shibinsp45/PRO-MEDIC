package com.example.medicalvending;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Admin extends AppCompatActivity {

    private long paracetamolStock;
    private long aspirinStock;
    private TextInputEditText paracetamolStockView, aspirinStockView;
    Button logoutButton, updateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        logoutButton = findViewById(R.id.logout);
        updateButton = findViewById(R.id.update);
        paracetamolStockView = findViewById(R.id.paracetamolstock);
        aspirinStockView = findViewById(R.id.aspirinstock);

        DatabaseReference myRef = FirebaseDatabase.getInstance("https://pro-medic-3db93-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("stock");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                paracetamolStock = (long) dataSnapshot.child("Paracetamol").getValue();
                aspirinStock = (long) dataSnapshot.child("Aspirin").getValue();
                paracetamolStockView.setText("" + paracetamolStock);
                aspirinStockView.setText("" + aspirinStock);
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

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paracetamolStock = Long.parseLong(paracetamolStockView.getText().toString());
                aspirinStock = Long.parseLong(aspirinStockView.getText().toString());
                myRef.child("Paracetamol").setValue(paracetamolStock);
                myRef.child("Aspirin").setValue(aspirinStock);
                Toast.makeText(Admin.this, "Successfully updated",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}