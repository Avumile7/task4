package com.SettingsActivity.kt

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.MainActivity2.kt.MainActivity2
import com.example.myapplication.R

class SettingsActivity : AppCompatActivity() {

    private lateinit var dayNameTextView: TextView
    private lateinit var tempEditText: EditText
    private lateinit var conditionEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    // Local copies of data received from MainActivity.kt
    private var days: Array<String> = arrayOf()
    private var maxTemps: Array<Int> = arrayOf()
    private var conditions: Array<String> = arrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        dayNameTextView = findViewById(R.id.dayNameTextView)
        tempEditText = findViewById(R.id.tempEditText)
        conditionEditText = findViewById(R.id.conditionEditText)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)

        // Retrieve data passed from MainActivity2
        intent.getStringArrayExtra(SettingsActivity.EXTRA_DAYS)?.let { days = it }
        intent.getIntArrayExtra(MainActivity2.EXTRA_MAX_TEMPS)?.let { tempArray ->
            maxTemps = tempArray.toTypedArray()
        }
        intent.getStringArrayExtra(MainActivity2.EXTRA_CONDITIONS)?.let { conditions = it }

        // For simplicity, let's allow editing only the first day's data for now.
        // You can expand this to allow editing all days.
        if (days.isNotEmpty() && maxTemps.isNotEmpty() && conditions.isNotEmpty()) {
            dayNameTextView.text = "Edit ${days[0]} Weather:"
            tempEditText.setText(maxTemps[0].toString())
            conditionEditText.setText(conditions[0])
        } else {
            Toast.makeText(this, "No weather data available to edit.", Toast.LENGTH_LONG).show()
            finish() // Close if no data
            return
        }

        saveButton.setOnClickListener {
            saveDataAndReturn()
        }

        cancelButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED) // Indicate that no changes were made
            finish() // Go back without saving
        }
    }

    object EXTRA_DAYS {

    }

    private fun saveDataAndReturn() {
        val newTempString = tempEditText.text.toString()
        val newCondition = conditionEditText.text.toString().trim()

        if (newTempString.isEmpty() || newCondition.isEmpty()) {
            Toast.makeText(this, "Please enter both temperature and condition.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val newTemp = newTempString.toInt()
            if (newTemp < -50 || newTemp > 50) { // Simple validation
                Toast.makeText(this, "Temperature must be between -50 and 50.", Toast.LENGTH_SHORT).show()
                return
            }

            // Update the first element of our local arrays
            maxTemps[0] = newTemp
            conditions[0] = newCondition

            // Prepare the intent to send updated data back to MainWeatherActivity
            val resultIntent = Intent().apply {
                putExtra(MainActivity2.EXTRA_DAYS, days)
                putExtra(MainActivity2.EXTRA_MAX_TEMPS, maxTemps.toIntArray())
                putExtra(MainActivity2.EXTRA_CONDITIONS, conditions)
            }
            setResult(Activity.RESULT_OK, resultIntent) // Indicate success and attach data
            finish() // Go back to MainWeatherActivity
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Invalid temperature. Please enter a number.", Toast.LENGTH_SHORT).show()
        }
    }
}