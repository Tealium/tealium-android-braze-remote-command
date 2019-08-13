package com.tealium.example;

import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.tealium.example.helper.TealiumHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EngagementActivity extends AppCompatActivity {

    public static final String TAG = EngagementActivity.class.getSimpleName();

    EditText mFacebookUserIdEditText;
    EditText mFacebookFriendCountEditText;
    EditText mTwitterUserIdEditText;
    EditText mTwitterDescriptionEditText;

    RadioGroup mEmailSubscriptionRadioGroup;
    RadioGroup mPushSubscriptionRadioGroup;

    AppCompatButton mSaveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_engagement);

        mFacebookUserIdEditText = findViewById(R.id.edit_txt_fb_username);
        mFacebookFriendCountEditText = findViewById(R.id.edit_txt_fb_friend_count);
        mTwitterUserIdEditText = findViewById(R.id.edit_txt_tw_id);
        mTwitterDescriptionEditText = findViewById(R.id.edit_txt_tw_user_description);

        mEmailSubscriptionRadioGroup = findViewById(R.id.radio_grp_email_subscription);
        mPushSubscriptionRadioGroup = findViewById(R.id.radio_grp_push_subscription);

        mSaveButton = findViewById(R.id.btn_save_engagement_details);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> data = getFieldData();
                TealiumHelper.trackEvent("setengagement", data);
                TealiumHelper.trackEvent("facebook", data);
                TealiumHelper.trackEvent("twitter", data);
            }
        });
    }

    private Map<String, Object> getFieldData() {
        Map<String, Object> data = new HashMap<>();

        data.put("facebook_user", mFacebookUserIdEditText.getText().toString());
        data.put("facebook_friends_count", mFacebookFriendCountEditText.getText().toString());

        data.put("twitter_user", mTwitterUserIdEditText.getText().toString());
        data.put("twitter_user_description", mTwitterDescriptionEditText.getText().toString());

        if (mEmailSubscriptionRadioGroup.getCheckedRadioButtonId() != -1) {
            data.put("email_subscription", getSubscriptionTypeFromRadioButton(mEmailSubscriptionRadioGroup.getCheckedRadioButtonId()));
        }

        if (mPushSubscriptionRadioGroup.getCheckedRadioButtonId() != -1) {
            data.put("push_subscription", getSubscriptionTypeFromRadioButton(mPushSubscriptionRadioGroup.getCheckedRadioButtonId()));
        }

        return data;
    }

    private String getSubscriptionTypeFromRadioButton(int id) {
        RadioButton selection = findViewById(id);
        if (selection.getText().toString().equals(getString(R.string.hint_opted_in))) {
            return getString(R.string.hint_opted_in_value);
        } else if (selection.getText().toString().equals(getString(R.string.hint_subscribed))) {
            return getString(R.string.hint_subscribed_value);
        } else if (selection.getText().toString().equals(getString(R.string.hint_unsubscribed))) {
            return getString(R.string.hint_unsubscribed_value);
        }
        return "";
    }


}
