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
    private lateinit var logProductViewedButton: AppCompatButton
    private lateinit var logCartUpdatedAddButton: AppCompatButton
    private lateinit var logCartUpdatedRemoveButton: AppCompatButton
    private lateinit var logCartUpdatedReplaceButton: AppCompatButton
    private lateinit var logCheckoutStartedButton: AppCompatButton
    private lateinit var logOrderPlacedButton: AppCompatButton
    private lateinit var logOrderCancelledButton: AppCompatButton
    private lateinit var logOrderRefundedButton: AppCompatButton
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
        logProductViewedButton = findViewById(R.id.btn_log_product_viewed)
        logCartUpdatedAddButton = findViewById(R.id.btn_log_cart_updated_add)
        logCartUpdatedRemoveButton = findViewById(R.id.btn_log_cart_updated_remove)
        logCartUpdatedReplaceButton = findViewById(R.id.btn_log_cart_updated_replace)
        logCheckoutStartedButton = findViewById(R.id.btn_log_checkout_started)
        logOrderPlacedButton = findViewById(R.id.btn_log_order_placed)
        logOrderCancelledButton = findViewById(R.id.btn_log_order_cancelled)
        logOrderRefundedButton = findViewById(R.id.btn_log_order_refunded)

        logEventButton.setOnClickListener { logEvent() }
        logEventWithPropertiesButton.setOnClickListener { logEventWithProperties() }
        setCustomAttributesButton.setOnClickListener { setCustomAttributes() }
        unsetCustomAttributesButton.setOnClickListener { unsetCustomAttributes() }
        incrementCustomAttributesButton.setOnClickListener { incrementCustomAttributes() }
        logPurchaseButton.setOnClickListener { logPurchase() }
        logMultiplePurchaseButton.setOnClickListener { logMultiplePurchase() }
        logProductViewedButton.setOnClickListener { logProductViewed() }
        logCartUpdatedAddButton.setOnClickListener { logCartUpdatedAdd() }
        logCartUpdatedRemoveButton.setOnClickListener { logCartUpdatedRemove() }
        logCartUpdatedReplaceButton.setOnClickListener { logCartUpdatedReplace() }
        logCheckoutStartedButton.setOnClickListener { logCheckoutStarted() }
        logOrderPlacedButton.setOnClickListener { logOrderPlaced() }
        logOrderCancelledButton.setOnClickListener { logOrderCancelled() }
        logOrderRefundedButton.setOnClickListener { logOrderRefunded() }
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
                "pet" to "cat",
                "pet_count" to 3,
                "pet_array" to listOf("one", "two", "three"),
                "pet_map" to mapOf("key1" to "one", "key2" to "two"),
                "pet_objects" to listOf(
                    mapOf("key1" to "one", "key2" to "two"),
                    mapOf("key3" to "three", "key4" to "four"),
                )
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
        trackEvent(
            "log_purchase", mapOf(
                "product_id" to "sku123",
                "order_currency" to "USD",
                "price" to 1.99,
                "rewards_member" to true,
                "rewards_points_earned" to 50
            )
        )
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

    private fun logProductViewed() {
        // logProductViewed describes a single product, so its fields are plain scalars (no arrays).
        trackEvent(
            "log_product_viewed", mapOf(
                "ec_product_id" to "sku123",
                "ec_product_name" to "Widget",
                "ec_variant_id" to "widget_blue_lg",
                "ec_price" to 49.99,
                "ec_currency" to "USD",
                "ec_source" to "android-example"
            )
        )
    }

    private fun logCartUpdatedAdd() {
        trackEvent("log_cart_updated_add", cartData())
    }

    private fun logCartUpdatedRemove() {
        trackEvent("log_cart_updated_remove", cartData())
    }

    private fun logCartUpdatedReplace() {
        // Replace is a full snapshot and requires total_value.
        trackEvent("log_cart_updated_replace", cartData() + mapOf("ec_total_value" to 109.96))
    }

    private fun logCheckoutStarted() {
        trackEvent(
            "log_checkout_started", cartData() + mapOf(
                "ec_checkout_id" to "checkout-123",
                "ec_total_value" to 109.96
            )
        )
    }

    private fun logOrderPlaced() {
        trackEvent(
            "log_order_placed", cartData() + mapOf(
                "ec_order_id" to "order-789",
                "ec_total_value" to 109.96,
                "ec_total_discounts" to 5.0,
                "ec_discounts" to listOf(exampleDiscount())
            )
        )
    }

    private fun logOrderCancelled() {
        trackEvent(
            "log_order_cancelled", cartData() + mapOf(
                "ec_order_id" to "order-789",
                "ec_total_value" to 109.96,
                "ec_total_discounts" to 5.0,
                "ec_discounts" to listOf(exampleDiscount()),
                "ec_cancel_reason" to "customer_request"
            )
        )
    }

    private fun logOrderRefunded() {
        trackEvent(
            "log_order_refunded", cartData() + mapOf(
                "ec_order_id" to "order-789",
                "ec_total_value" to 109.96,
                "ec_total_discounts" to 5.0,
                "ec_discounts" to listOf(exampleDiscount())
            )
        )
    }

    // Sample multi-product cart payload shared by the cart / checkout / order demo events. Products
    // are supplied as PARALLEL TOP-LEVEL ARRAYS (one array per field, zipped by index).
    private fun cartData(): Map<String, Any> = mapOf(
        "ec_cart_id" to "cart-456",
        "ec_currency" to "USD",
        "ec_source" to "android-example",
        "ec_product_id" to listOf("sku123", "sku456"),
        "ec_product_name" to listOf("Widget", "Gadget"),
        "ec_variant_id" to listOf("widget_blue_lg", "gadget_red"),
        "ec_price" to listOf(49.99, 19.99),
        "ec_quantity" to listOf(1, 3)
    )

    // Builds a single discount entry using the wrapper's raw discount keys. Values nested inside an
    // array like this are passed straight through without friendly-name remapping.
    private fun exampleDiscount(): Map<String, Any> = mapOf(
        "code" to "SUMMER10",
        "amount" to 5.0,
        "type" to "percentage"
    )

    companion object {
        private val TAG = EventsActivity::class.java.simpleName
    }
}