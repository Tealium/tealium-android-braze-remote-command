package com.tealium.example;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.tealium.example.helper.TealiumHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EventsActivity extends AppCompatActivity {

    private static final String TAG = EventsActivity.class.getSimpleName();

    AppCompatButton mLogEventButton;
    AppCompatButton mLogEventWithPropertiesButton;
    AppCompatButton mSetCustomAttributesButton;
    AppCompatButton mUnsetCustomAttributesButton;
    AppCompatButton mIncrementCustomAttributesButton;
    AppCompatButton mLogPurchaseButton;

    int mPetNameCounter = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_events);

        mLogEventButton = findViewById(R.id.btn_log_event);
        mLogEventWithPropertiesButton = findViewById(R.id.btn_log_event_with_properties);
        mSetCustomAttributesButton = findViewById(R.id.btn_set_custom_attributes);
        mUnsetCustomAttributesButton = findViewById(R.id.btn_unset_custom_attributes);
        mIncrementCustomAttributesButton = findViewById(R.id.btn_increment_custom_attributes);
        mLogPurchaseButton = findViewById(R.id.btn_log_purchase);

        mLogEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logEvent();
            }
        });

        mLogEventWithPropertiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logEventWithProperties();
            }
        });

        mSetCustomAttributesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCustomAttributes();
            }
        });

        mUnsetCustomAttributesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsetCustomAttributes();
            }
        });

        mIncrementCustomAttributesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementCustomAttributes();
            }
        });

        mLogPurchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logPurchase();
            }
        });

    }

    private void logEvent() {
        Map<String, Object> data = new HashMap<>();
        data.put("event_name", "custom_event");

        TealiumHelper.trackEvent("log_custom_event", data);
    }

    private void logEventWithProperties() {
        Map<String, Object> data = new HashMap<>();
        data.put("event_name", "custom_event_with_properties");
        try {
            JSONObject props = new JSONObject();
            props.put("key1", "value1");
            props.put("key2", "value2");
            data.put("event_properties", props);
        } catch (JSONException jex) {
            Log.d(TAG, "logEventWithProperties: " + jex.getMessage());
        }
        TealiumHelper.trackEvent("log_custom_event", data);
    }

    private void setCustomAttributes() {
        Map<String, Object> data = new HashMap<>();
        data.put("pet", "cat");
        data.put("pet_count", 3);

        TealiumHelper.trackEvent("custom_attribute", data);

        Map<String, Object> arrayData = new HashMap<>();
        arrayData.put("pet_names", new String[]{"Rosia", "Elsa", "Kawai"});

        TealiumHelper.trackEvent("custom_array_attribute", arrayData);
    }

    private void unsetCustomAttributes() {
        Map<String, Object> data = new HashMap<>();
        data.put("pet_count_unset", new String[]{"pet_count"});

        TealiumHelper.trackEvent("unset_custom_attribute", data);

        Map<String, Object> arrayData = new HashMap<>();
        arrayData.put("pet_names_remove", "Kawai");

        TealiumHelper.trackEvent("remove_custom_array_attribute", arrayData);
    }

    private void incrementCustomAttributes() {
        Map<String, Object> data = new HashMap<>();
        data.put("pet_count_increment", 2);

        TealiumHelper.trackEvent("increment_custom_attribute", data);

        Map<String, Object> appendData = new HashMap<>();
        appendData.put("pet_names_append", "petname" + mPetNameCounter++);

        TealiumHelper.trackEvent("append_custom_array_attribute", appendData);
    }

    private void logPurchase() {
        Map<String, Object> data = new HashMap<>();
        data.put("product_id", "sku123");
        data.put("order_currency", "USD");
        data.put("price", 1.99);

        TealiumHelper.trackEvent("log_purchase", data);
    }

}
