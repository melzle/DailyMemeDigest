package com.sukacita.dailymemedigest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnSave: Button = findViewById(R.id.btnSave_SettingsActivity)

        btnSave.setOnClickListener() {
//            val intent: Intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
        }
    }
}