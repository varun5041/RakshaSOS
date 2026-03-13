package com.example.myapplication;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationClient;

    Button sosBtn, policeBtn, hospitalBtn;

    String phone1, phone2, phone3;

    double latitude;
    double longitude;

    String userPincode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        sosBtn = findViewById(R.id.sosBtn);
        policeBtn = findViewById(R.id.policeBtn);
        hospitalBtn = findViewById(R.id.hospitalBtn);

        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);

        phone1 = prefs.getString("phone1", "");
        phone2 = prefs.getString("phone2", "");
        phone3 = prefs.getString("phone3", "");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getUserLocation();

        sosBtn.setOnClickListener(v -> sendEmergencySMS("SOS EMERGENCY"));

        policeBtn.setOnClickListener(v -> sendEmergencySMS("POLICE HELP NEEDED"));

        hospitalBtn.setOnClickListener(v -> sendEmergencySMS("MEDICAL EMERGENCY"));
    }

    private void getUserLocation() {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this,"Location permission missing",Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
        ).addOnSuccessListener(location -> {

            if (location != null) {

                latitude = location.getLatitude();
                longitude = location.getLongitude();

                convertLocationToAddress(location);

            } else {

                Toast.makeText(this,"Unable to get location",Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void convertLocationToAddress(Location location) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {

            List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1
            );

            if (addresses != null && !addresses.isEmpty()) {

                Address address = addresses.get(0);

                userPincode = address.getPostalCode();

                SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                editor.putString("pincode", userPincode);
                editor.putString("latitude", String.valueOf(latitude));
                editor.putString("longitude", String.valueOf(longitude));

                editor.apply();

                Toast.makeText(this,"Pincode: " + userPincode,Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {

            Toast.makeText(this,"Geocoder error",Toast.LENGTH_SHORT).show();
        }
    }

    private void sendEmergencySMS(String type) {

        String message = type + "\n\nI need help.\n\nMy location:\n"
                + "https://maps.google.com/?q=" + latitude + "," + longitude;

        try {

            SmsManager smsManager = SmsManager.getDefault();

            smsManager.sendTextMessage(phone1, null, message, null, null);
            smsManager.sendTextMessage(phone2, null, message, null, null);
            smsManager.sendTextMessage(phone3, null, message, null, null);

            Toast.makeText(this,"Alert Sent Successfully",Toast.LENGTH_LONG).show();

        } catch (Exception e) {

            Toast.makeText(this,"SMS Failed: "+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
}