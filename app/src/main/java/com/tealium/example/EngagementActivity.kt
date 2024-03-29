package com.tealium.example

import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.tealium.example.helper.TealiumHelper.trackEvent

class EngagementActivity : AppCompatActivity() {
    private lateinit var emailSubscriptionRadioGroup: RadioGroup
    private lateinit var pushSubscriptionRadioGroup: RadioGroup
    private lateinit var saveButton: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_engagement)
        emailSubscriptionRadioGroup = findViewById(R.id.radio_grp_email_subscription)
        pushSubscriptionRadioGroup = findViewById(R.id.radio_grp_push_subscription)
        saveButton = findViewById(R.id.btn_save_engagement_details)
        saveButton.setOnClickListener {
            val data = fieldData
            trackEvent("setengagement", data)
            data["email_subscription"]?.let { emailSubscription ->
                if (emailSubscription == getString(R.string.hint_subscribed_value)) {
                    trackEvent("subscribed", mapOf("subscription_group" to "email"))
                } else if (emailSubscription == getString(R.string.hint_unsubscribed_value)) {
                    trackEvent("unsubscribed", mapOf("subscription_group" to "email"))
                }
            }

            data["push_subscription"]?.let { pushSubscription ->
                if (pushSubscription == getString(R.string.hint_subscribed_value)) {
                    trackEvent("subscribed", mapOf("subscription_group" to "push"))
                } else if (pushSubscription == getString(R.string.hint_unsubscribed_value)) {
                    trackEvent("unsubscribed", mapOf("subscription_group" to "push"))
                }
            }
        }
    }

    private val fieldData: Map<String, Any>
        get() {
            val data = mutableMapOf<String, Any>()
            if (emailSubscriptionRadioGroup.checkedRadioButtonId != -1) {
                data["email_subscription"] = getSubscriptionTypeFromRadioButton(emailSubscriptionRadioGroup.checkedRadioButtonId)
            }
            if (pushSubscriptionRadioGroup.checkedRadioButtonId != -1) {
                data["push_subscription"] = getSubscriptionTypeFromRadioButton(pushSubscriptionRadioGroup.checkedRadioButtonId)
            }
            return data
        }

    private fun getSubscriptionTypeFromRadioButton(id: Int): String {
        val selection = findViewById<RadioButton>(id)
        if (selection.text.toString() == getString(R.string.hint_opted_in)) {
            return getString(R.string.hint_opted_in_value)
        } else if (selection.text.toString() == getString(R.string.hint_subscribed)) {
            return getString(R.string.hint_subscribed_value)
        } else if (selection.text.toString() == getString(R.string.hint_unsubscribed)) {
            return getString(R.string.hint_unsubscribed_value)
        }
        return ""
    }

    companion object {
        val TAG = EngagementActivity::class.java.simpleName
    }
}