package com.tealium.example

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.tealium.example.helper.TealiumHelper.trackEvent
import java.text.SimpleDateFormat
import java.util.*

class UserActivity : AppCompatActivity() {

    lateinit var firstNameEditText: EditText
    lateinit var lastNameEditText: EditText
    lateinit var emailEditText: EditText
    lateinit var genderEditText: EditText
    lateinit var homeCityEditText: EditText
    lateinit var birthdayEditText: EditText
    lateinit var userIdEditText: EditText
    lateinit var userAliasEditText: EditText
    lateinit var userAliasLabelEditText: EditText
    lateinit var saveButton: AppCompatButton
    lateinit var calendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        firstNameEditText = findViewById(R.id.edit_txt_first_name)
        lastNameEditText = findViewById(R.id.edit_txt_last_name)
        emailEditText = findViewById(R.id.edit_txt_email)
        genderEditText = findViewById(R.id.edit_txt_gender)
        homeCityEditText = findViewById(R.id.edit_txt_home_city)
        birthdayEditText = findViewById(R.id.edit_txt_birthday)
        userIdEditText = findViewById(R.id.edit_txt_user_id)
        userAliasEditText = findViewById(R.id.edit_txt_user_alias)
        userAliasLabelEditText = findViewById(R.id.edit_txt_user_alias_label)
        saveButton = findViewById(R.id.btn_save_user_details)
        calendar = Calendar.getInstance()
        val date = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateLabel()
        }
        birthdayEditText.setOnClickListener {
            DatePickerDialog(this@UserActivity, date, calendar
                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
        saveButton.setOnClickListener {
            val data = fieldData
            trackEvent("user_attribute", data)
            trackEvent("user_login", data)
            trackEvent("user_alias", data)
        }
    }

    private fun updateDateLabel() {
        val dataFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(dataFormat, Locale.getDefault())
        birthdayEditText.setText(sdf.format(calendar.time))
    }

    private val fieldData: Map<String, String>
        get() {
            val data = mutableMapOf<String, String>()
            data["customer_first_name"] = firstNameEditText.text.toString()
            data["customer_last_name"] = lastNameEditText.text.toString()
            data["customer_email"] = emailEditText.text.toString()
            var gender = genderEditText.text.toString()
            if (TextUtils.isEmpty(gender)) {
                gender = "prefernotosay"
            }
            data["customer_gender"] = gender
            data["customer_home_city"] = homeCityEditText.text.toString()
            data["customer_dob"] = birthdayEditText.text.toString()
            data["customer_id"] = userIdEditText.text.toString()
            data["customer_alias"] = userAliasEditText.text.toString()
            data["customer_alias_label"] = userAliasLabelEditText.text.toString()
            return data
        }
}