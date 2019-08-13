package com.tealium.example;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    AppCompatButton mEditUserDetailsButton;
    AppCompatButton mEventsAndAttributesButton;
    AppCompatButton mEditUsetEngagementsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditUserDetailsButton = findViewById(R.id.btn_edit_user_details);
        mEditUserDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UserActivity.class));
            }
        });

        mEventsAndAttributesButton = findViewById(R.id.btn_send_events);
        mEventsAndAttributesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EventsActivity.class));
            }
        });

        mEditUsetEngagementsButton = findViewById(R.id.btn_edit_user_engagement);
        mEditUsetEngagementsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EngagementActivity.class));
            }
        });
    }
}
