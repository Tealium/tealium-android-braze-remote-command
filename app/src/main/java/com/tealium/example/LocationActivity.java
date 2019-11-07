package com.tealium.example;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.appboy.services.AppboyLocationService;

import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class LocationActivity extends AppCompatActivity {

    public static final String TAG = LocationActivity.class.getSimpleName();
    public static final int LOCATION_PERMISSION_REQUEST = 100;

    private TextView mLocationPermissionStatus;
    private Button mRequestPermissionButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        mLocationPermissionStatus = findViewById(R.id.txt_location_permission);
        mRequestPermissionButton = findViewById(R.id.btn_request_location_permission);
        mRequestPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLocationPermissionsIfRequired();
            }
        });

        requestLocationPermissionsIfRequired();
    }

    private void requestLocationPermissionsIfRequired() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (ContextCompat.checkSelfPermission(LocationActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    ActivityCompat.requestPermissions(LocationActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION_REQUEST);
                } else {
                    updateUI(PERMISSION_GRANTED);
                }
            }
        }).run();
    }

    private void updateUI(int status) {
        String statusString = "Unknown";
        switch (status) {
            case PERMISSION_GRANTED:
                mRequestPermissionButton.setEnabled(false);
                statusString = "Granted";
                break;
            case PERMISSION_DENIED:
                mRequestPermissionButton.setEnabled(true);
                statusString = "Denied";
                break;
        }
        mLocationPermissionStatus.setText(
                getString(R.string.txt_location_permissions, statusString)
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST:
                if (grantResults[0] == PERMISSION_GRANTED) {
                    Log.i(TAG, "Location permission granted.");
                    updateUI(PERMISSION_GRANTED);
                    Toast.makeText(this, "Location permission granted.", Toast.LENGTH_SHORT).show();
                    AppboyLocationService.requestInitialization(this);
                } else {
                    updateUI(PERMISSION_DENIED);
                    Log.i(TAG, "Location permission NOT granted.");
                    Toast.makeText(this, "Location permission NOT granted.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
