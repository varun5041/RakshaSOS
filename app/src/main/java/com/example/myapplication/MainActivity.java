package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText phone1, phone2, phone3;
    Button saveBtn;

    boolean permissionsGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionsGranted = getIntent().getBooleanExtra("permissionsGranted", false);

        phone1 = findViewById(R.id.phone1);
        phone2 = findViewById(R.id.phone2);
        phone3 = findViewById(R.id.phone3);
        saveBtn = findViewById(R.id.saveBtn);

        saveBtn.setOnClickListener(v -> saveContacts());
    }

    private void saveContacts() {

        String p1 = phone1.getText().toString();
        String p2 = phone2.getText().toString();
        String p3 = phone3.getText().toString();

        if(p1.isEmpty() || p2.isEmpty() || p3.isEmpty()){
            Toast.makeText(this,"Enter all numbers",Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("phone1", p1);
        editor.putString("phone2", p2);
        editor.putString("phone3", p3);

        editor.putBoolean("isRegistered", true);

        editor.apply();

        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
        intent.putExtra("permissionsGranted", permissionsGranted);

        startActivity(intent);
        finish();
    }
}