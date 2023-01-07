package com.sukacita.dailymemedigest

import android.annotation.SuppressLint
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
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // coba coba aja
//        val sharedFile = "com.sukacita.dailymemedigest"
//        var shared : SharedPreferences = getSharedPreferences(sharedFile, Context.MODE_PRIVATE)
//        val user = shared.getString("user", null)
//        val obj = JSONObject(user)
//        val data = obj.getJSONArray("data")
//        val userObj = data.getJSONObject(0)
//
//        val txt: TextView = findViewById(R.id.textView4)
//        txt.text = userObj.getString("lastname")


        val btnCreateAcc: Button = findViewById(R.id.btnCreateAcc_RegisterActivity)
        val txtUsername: TextView = findViewById(R.id.txtUsername_RegisterActivity)
        val txtPass: TextView = findViewById(R.id.txtPass_RegisterActivity)
        val txtPassRepeat: TextView = findViewById(R.id.txtPassRepeat_RegisterActivity)

        btnCreateAcc.setOnClickListener() {
            val username = txtUsername.text
            val pass= txtPass.text.toString()
            val passRepeat = txtPassRepeat.text.toString()

            if (pass == passRepeat) {
                register(username.toString(), pass.toString())
            } else {
                Toast.makeText(this, "Passwords don't match. Please check your password again", Toast.LENGTH_SHORT).show()
            }

        }


    }

    private fun register(username: String, password: String) {
        val q = Volley.newRequestQueue(this)
        val url = "https://scheday.site/nmp/register.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> {
                Log.d("apiresult", it)
                val obj = JSONObject(it)
                if(obj.getString("result") == "OK") {
                    Toast.makeText(this, "Registration Successful. Please Log in", Toast.LENGTH_SHORT).show()
                    finish()
                } else if (obj.getString("result") == "ERROR_USERNAME") {
                    Toast.makeText(this, "Username is already taken. Please use another username", Toast.LENGTH_SHORT).show()
                }},
            Response.ErrorListener {
                Log.d("cekparams", it.message.toString())
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["username"] = username
                params["password"] = password
                return params
            }
        }
        q.add(stringRequest)
    }
}