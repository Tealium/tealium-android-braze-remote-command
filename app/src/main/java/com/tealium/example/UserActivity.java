package com.tealium.example;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.tealium.example.helper.TealiumHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UserActivity extends AppCompatActivity {

    EditText mFirstNameEditText;
    EditText mLastNameEditText;
    EditText mEmailEditText;
    EditText mGenderEditText;
    EditText mHomeCityEditText;
    EditText mBirthdayEditText;
    EditText mUserIdEditText;
    EditText mUserAliasEditText;
    EditText mUserAliasLabelEditText;

    AppCompatButton mSaveButton;

    Calendar mCal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mFirstNameEditText = findViewById(R.id.edit_txt_first_name);
        mLastNameEditText = findViewById(R.id.edit_txt_last_name);
        mEmailEditText = findViewById(R.id.edit_txt_email);
        mGenderEditText = findViewById(R.id.edit_txt_gender);
        mHomeCityEditText = findViewById(R.id.edit_txt_home_city);
        mBirthdayEditText = findViewById(R.id.edit_txt_birthday);
        mUserIdEditText = findViewById(R.id.edit_txt_user_id);
        mUserAliasEditText = findViewById(R.id.edit_txt_user_alias);
        mUserAliasLabelEditText = findViewById(R.id.edit_txt_user_alias_label);
        mSaveButton = findViewById(R.id.btn_save_user_details);

        mCal = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                mCal.set(Calendar.YEAR, year);
                mCal.set(Calendar.MONTH, monthOfYear);
                mCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateLabel();
            }

        };

        mBirthdayEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(UserActivity.this, date, mCal
                        .get(Calendar.YEAR), mCal.get(Calendar.MONTH),
                        mCal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> data = getFieldData();
                TealiumHelper.trackEvent("user_attribute", data);
                TealiumHelper.trackEvent("user_login", data);
                TealiumHelper.trackEvent("user_alias", data);
            }
        });

    }

    private void updateDateLabel() {
        String dataFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dataFormat, Locale.getDefault());

        mBirthdayEditText.setText(sdf.format(mCal.getTime()));
    }

    private Map<String, String> getFieldData() {
        Map<String, String> data = new HashMap<>();

        data.put("first_name", mFirstNameEditText.getText().toString());
        data.put("last_name", mLastNameEditText.getText().toString());
        data.put("email", mEmailEditText.getText().toString());
        String gender = mGenderEditText.getText().toString();
        if (TextUtils.isEmpty(gender)) {
            gender = "prefernotosay";
        }
        data.put("gender", gender);
        data.put("home_city", mHomeCityEditText.getText().toString());
        data.put("birthday", mBirthdayEditText.getText().toString());

        data.put("user_id", mUserIdEditText.getText().toString());
        data.put("user_alias", mUserAliasEditText.getText().toString());
        data.put("user_alias_label", mUserAliasLabelEditText.getText().toString());

        return data;
    }
}
