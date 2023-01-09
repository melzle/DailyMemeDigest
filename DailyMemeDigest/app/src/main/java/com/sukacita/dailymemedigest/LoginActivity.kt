package com.sukacita.dailymemedigest

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
//import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnSignIn: Button = findViewById(R.id.btnSignIn_LoginActivity)
        val btnRegister: Button = findViewById(R.id.btnCreateAcc_LoginActivity)
        val txtUsername: TextView = findViewById(R.id.txtUsername)
        val txtPassword: TextView = findViewById(R.id.txtPassword)

        val sharedFile = "com.sukacita.dailymemedigest"
        var shared : SharedPreferences = getSharedPreferences(sharedFile, Context.MODE_PRIVATE)

        btnSignIn.setOnClickListener() {
            val username = txtUsername.text
            val password = txtPassword.text

            val q = Volley.newRequestQueue(this)
            val url = "https://scheday.site/nmp/login.php"

            val stringRequest = object : StringRequest(
                Request.Method.POST, url,
                Response.Listener<String> {
//                    Log.d("apiresult", it)
                    val obj = JSONObject(it)
                    if(obj.getString("result") == "OK") {
                        var editor : SharedPreferences.Editor = shared.edit()
                        editor.putString("user", it)
                        editor.apply()

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Invalid credentials. Please check your username and password", Toast.LENGTH_SHORT).show()
                    }},
                Response.ErrorListener {
                    Log.d("cekparams", it.message.toString())
                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params["username"] = username.toString()
                    params["password"] = password.toString()
                    return params
                }
            }
            q.add(stringRequest)
        }

        btnRegister.setOnClickListener() {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

}