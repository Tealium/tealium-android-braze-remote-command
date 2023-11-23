package com.tealium.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.tealium.example.helper.TealiumHelper.trackEvent

class EventsActivity : AppCompatActivity() {
    private lateinit var logEventButton: AppCompatButton
    private lateinit var logEventWithPropertiesButton: AppCompatButton
    private lateinit var setCustomAttributesButton: AppCompatButton
    private lateinit var unsetCustomAttributesButton: AppCompatButton
    private lateinit var incrementCustomAttributesButton: AppCompatButton
    private lateinit var logPurchaseButton: AppCompatButton
    private lateinit var logMultiplePurchaseButton: AppCompatButton
    private var petNameCounter = 1

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
        trackEvent("log_custom_event", mapOf("event_name" to "custom_event"))
    }

    private fun logEventWithProperties() {
        trackEvent(
            "log_custom_event", mapOf(
                "event_name" to "custom_event_with_properties",
                "current_level" to 10, "high_score" to 5000
            )
        )

    }

    private fun setCustomAttributes() {
        trackEvent(
            "custom_attribute", mapOf<String, Any>(
                "pet" to "cat", "pet_count" to 3
            )
        )

        trackEvent(
            "custom_array_attribute",
            mapOf("pet_names" to arrayOf("Rosia", "Elsa", "Kawai"))
        )
    }

    private fun unsetCustomAttributes() {
        trackEvent(
            "unset_custom_attribute", mapOf(
                "pet_count_unset" to arrayOf("pet_count", "something_else")
            )
        )

        trackEvent(
            "remove_custom_array_attribute", mapOf(
                "pet_names_remove" to "Kawai"
            )
        )
    }

    private fun incrementCustomAttributes() {
        trackEvent("increment_custom_attribute", mapOf("pet_count_increment" to 2))
        trackEvent(
            "append_custom_array_attribute",
            mapOf("pet_names_append" to "petname" + petNameCounter++)
        )
    }

    private fun logPurchase() {
        trackEvent("log_purchase", mapOf(
            "product_id" to "sku123",
            "order_currency" to "USD",
            "price" to 1.99,
            "rewards_member" to true,
            "rewards_points_earned" to 50
        ))
    }

    private fun logMultiplePurchase() {
        val props = mapOf(
            "rewards_member" to true,
            "rewards_points_earned" to 50
        )
        val data = mapOf(
            "product_id" to listOf("sku123", "sku456"),
            "order_currency" to "USD",
            "price" to listOf(1.99, 2.99),
            "product_quantity" to listOf(1, 2),
            "purchase_props" to listOf(
                props,
                props
            )
        )

        trackEvent("log_purchase", data)
    }

    companion object {
        private val TAG = EventsActivity::class.java.simpleName
    }
}