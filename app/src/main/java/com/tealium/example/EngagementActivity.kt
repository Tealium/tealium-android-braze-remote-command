package com.tealium.example

import android.os.Bundle
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.tealium.example.helper.TealiumHelper.trackEvent

class EngagementActivity : AppCompatActivity() {
    lateinit var facebookUserIdEditText: EditText
    lateinit var facebookFriendCountEditText: EditText
    lateinit var twitterUserIdEditText: EditText
    lateinit var twitterDescriptionEditText: EditText
    lateinit var emailSubscriptionRadioGroup: RadioGroup
    lateinit var pushSubscriptionRadioGroup: RadioGroup
    lateinit var saveButton: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_engagement)
        facebookUserIdEditText = findViewById(R.id.edit_txt_fb_username)
        facebookFriendCountEditText = findViewById(R.id.edit_txt_fb_friend_count)
        twitterUserIdEditText = findViewById(R.id.edit_txt_tw_id)
        twitterDescriptionEditText = findViewById(R.id.edit_txt_tw_user_description)
        emailSubscriptionRadioGroup = findViewById(R.id.radio_grp_email_subscription)
        pushSubscriptionRadioGroup = findViewById(R.id.radio_grp_push_subscription)
        saveButton = findViewById(R.id.btn_save_engagement_details)
        saveButton.setOnClickListener {
            val data = fieldData
            trackEvent("setengagement", data)
            trackEvent("facebook", data)
            trackEvent("twitter", data)
        }
    }

    private val fieldData: Map<String, Any>
        get() {
            val data = mutableMapOf<String, Any>()
            data["facebook_user"] = facebookUserIdEditText.text.toString()
            data["facebook_friends_count"] = facebookFriendCountEditText.text.toString()
            data["twitter_user"] = twitterUserIdEditText.text.toString()
            data["twitter_user_description"] = twitterDescriptionEditText.text.toString()
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