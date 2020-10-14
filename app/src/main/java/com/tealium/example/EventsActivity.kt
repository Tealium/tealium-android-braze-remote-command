package com.tealium.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.tealium.example.helper.TealiumHelper.trackEvent

class EventsActivity : AppCompatActivity() {
    lateinit var logEventButton: AppCompatButton
    lateinit var logEventWithPropertiesButton: AppCompatButton
    lateinit var setCustomAttributesButton: AppCompatButton
    lateinit var unsetCustomAttributesButton: AppCompatButton
    lateinit var incrementCustomAttributesButton: AppCompatButton
    lateinit var logPurchaseButton: AppCompatButton
    lateinit var logMultiplePurchaseButton: AppCompatButton
    var petNameCounter = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)
        logEventButton = findViewById(R.id.btn_log_event)
        logEventWithPropertiesButton = findViewById(R.id.btn_log_event_with_properties)
        setCustomAttributesButton = findViewById(R.id.btn_set_custom_attributes)
        unsetCustomAttributesButton = findViewById(R.id.btn_unset_custom_attributes)
        incrementCustomAttributesButton = findViewById(R.id.btn_increment_custom_attributes)
        logPurchaseButton = findViewById(R.id.btn_log_purchase)
        logMultiplePurchaseButton = findViewById(R.id.btn_log_multiple_purchase)

        logEventButton.setOnClickListener { logEvent() }
        logEventWithPropertiesButton.setOnClickListener { logEventWithProperties() }
        setCustomAttributesButton.setOnClickListener { setCustomAttributes() }
        unsetCustomAttributesButton.setOnClickListener { unsetCustomAttributes() }
        incrementCustomAttributesButton.setOnClickListener { incrementCustomAttributes() }
        logPurchaseButton.setOnClickListener { logPurchase() }
        logMultiplePurchaseButton.setOnClickListener { logMultiplePurchase() }
    }

    private fun logEvent() {
        val data = mutableMapOf<String, Any>()
        data["event_name"] = "custom_event"
        trackEvent("log_custom_event", data)
    }

    private fun logEventWithProperties() {
        val data = mutableMapOf<String, Any>()
        data["event_name"] = "custom_event_with_properties"
        data["current_level"] = 10
        data["high_score"] = 5000
        trackEvent("log_custom_event", data)
    }

    private fun setCustomAttributes() {
        val data = mutableMapOf<String, Any>()
        data["pet"] = "cat"
        data["pet_count"] = 3
        trackEvent("custom_attribute", data)
        val arrayData = mutableMapOf<String, Any>()
        arrayData["pet_names"] = arrayOf("Rosia", "Elsa", "Kawai")
        trackEvent("custom_array_attribute", arrayData)
    }

    private fun unsetCustomAttributes() {
        val data = mutableMapOf<String, Any>()
        data["pet_count_unset"] = arrayOf("pet_count", "something_else")
        trackEvent("unset_custom_attribute", data)
        val arrayData= mutableMapOf<String, Any>()
        arrayData["pet_names_remove"] = "Kawai"
        trackEvent("remove_custom_array_attribute", arrayData)
    }

    private fun incrementCustomAttributes() {
        val data = mutableMapOf<String, Any>()
        data["pet_count_increment"] = 2
        trackEvent("increment_custom_attribute", data)
        val appendData= mutableMapOf<String, Any>()
        appendData["pet_names_append"] = "petname" + petNameCounter++
        trackEvent("append_custom_array_attribute", appendData)
    }

    private fun logPurchase() {
        val data = mutableMapOf<String, Any>()
        data["product_id"] = "sku123"
        data["order_currency"] = "USD"
        data["product_unit_price"] = 1.99
        data["rewards_member"] = true
        data["rewards_points_earned"] = 50
        trackEvent("log_purchase", data)
    }

    private fun logMultiplePurchase() {
        val data = mutableMapOf<String, Any>()
        data["product_id"] = listOf("sku123", "sku456")
        data["order_currency"] = "USD"
        data["product_unit_price"] = listOf(1.99, 2.99)
        data["product_quantity"] = listOf(1, 2)

        val props = mapOf(
                "rewards_member" to true,
                "rewards_points_earned" to 50
        )
        data["purchase_props"] = listOf(
            props,
            props
        )

        trackEvent("log_purchase", data)
    }

    companion object {
        private val TAG = EventsActivity::class.java.simpleName
    }
}