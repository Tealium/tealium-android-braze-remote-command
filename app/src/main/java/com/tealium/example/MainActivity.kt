package com.tealium.example

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class MainActivity : AppCompatActivity() {

    lateinit var editUserDetailsButton: AppCompatButton
    lateinit var eventsAndAttributesButton: AppCompatButton
    lateinit var editUsetEngagementsButton: AppCompatButton
    lateinit var locationButton: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editUserDetailsButton = findViewById(R.id.btn_edit_user_details)
        editUserDetailsButton.setOnClickListener { startActivity(Intent(this@MainActivity, UserActivity::class.java)) }
        eventsAndAttributesButton = findViewById(R.id.btn_send_events)
        eventsAndAttributesButton.setOnClickListener { startActivity(Intent(this@MainActivity, EventsActivity::class.java)) }
        editUsetEngagementsButton = findViewById(R.id.btn_edit_user_engagement)
        editUsetEngagementsButton.setOnClickListener { startActivity(Intent(this@MainActivity, EngagementActivity::class.java)) }
        locationButton = findViewById(R.id.btn_location)
        locationButton.setOnClickListener { startActivity(Intent(this@MainActivity, LocationActivity::class.java)) }
    }
}