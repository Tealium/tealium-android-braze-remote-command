package com.tealium.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class LocationActivity : AppCompatActivity() {

    private lateinit var locationPermissionStatus: TextView
    private lateinit var requestPermissionButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        locationPermissionStatus = findViewById(R.id.txt_location_permission)
        requestPermissionButton = findViewById(R.id.btn_request_location_permission)
        requestPermissionButton.setOnClickListener { requestLocationPermissionsIfRequired() }
        requestLocationPermissionsIfRequired()
    }

    private fun requestLocationPermissionsIfRequired() {
        MainScope().launch {
            if (ContextCompat.checkSelfPermission(
                    this@LocationActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {

                // Permission is not granted
                ActivityCompat.requestPermissions(
                    this@LocationActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST
                )
            } else {
                updateUI(PackageManager.PERMISSION_GRANTED)
            }
        }
    }

    private fun updateUI(status: Int) {
        var statusString = "Unknown"
        when (status) {
            PackageManager.PERMISSION_GRANTED -> {
                requestPermissionButton.isEnabled = false
                statusString = "Granted"
            }

            PackageManager.PERMISSION_DENIED -> {
                requestPermissionButton.isEnabled = true
                statusString = "Denied"
            }
        }
        locationPermissionStatus.text = getString(R.string.txt_location_permissions, statusString)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Location permission granted.")
                updateUI(PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Location permission granted.", Toast.LENGTH_SHORT).show()
            } else {
                updateUI(PackageManager.PERMISSION_DENIED)
                Log.i(TAG, "Location permission NOT granted.")
                Toast.makeText(this, "Location permission NOT granted.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        val TAG = LocationActivity::class.java.simpleName
        const val LOCATION_PERMISSION_REQUEST = 100
    }
}