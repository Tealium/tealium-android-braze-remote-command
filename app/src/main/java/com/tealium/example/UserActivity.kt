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

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var genderEditText: EditText
    private lateinit var homeCityEditText: EditText
    private lateinit var birthdayEditText: EditText
    private lateinit var userIdEditText: EditText
    private lateinit var userAliasEditText: EditText
    private lateinit var userAliasLabelEditText: EditText
    private lateinit var saveButton: AppCompatButton
    private lateinit var calendar: Calendar

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
            DatePickerDialog(
                this@UserActivity, date, calendar
                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
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
        get() = mapOf(
            "customer_first_name" to firstNameEditText.text.toString(),
            "customer_last_name" to lastNameEditText.text.toString(),
            "customer_email" to emailEditText.text.toString(),
            "customer_gender" to (if (TextUtils.isEmpty(genderEditText.text))
                "prefernotosay"
            else genderEditText.text.toString()),
            "customer_home_city" to homeCityEditText.text.toString(),
            "customer_dob" to birthdayEditText.text.toString(),
            "customer_id" to userIdEditText.text.toString(),
            "customer_alias" to userAliasEditText.text.toString(),
            "customer_alias_label" to userAliasLabelEditText.text.toString(),
        )

}