package com.sukacita.dailymemedigest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnSignIn_LoginActivity.setOnClickListener() {
            val username = txtUsername.text
            val password = txtPassword.text

            val q = Volley.
        }
    }

}