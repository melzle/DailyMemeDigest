package com.sukacita.dailymemedigest

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        val imgProfile: ImageView = findViewById(R.id.imgProfile_SettingsActivity)
        val txtName: TextView = findViewById(R.id.txtName_SettingsActivity)
        val txtDate: TextView = findViewById(R.id.txtDate_SettingsActivity)
        val txtUsername: TextView = findViewById(R.id.txtUsername_SettingsActivity)
        val txtFirstName: TextView = findViewById(R.id.txtFirstName_SettingsActivity)
        val txtLastName: TextView = findViewById(R.id.txtLastName_SettingsActivity)
        val chkPrivacy: CheckBox = findViewById(R.id.chkPrivacy_SettingsActivity)
        val btnSave: Button = findViewById(R.id.btnSave_SettingsActivity)
        val fab: FloatingActionButton = findViewById(R.id.fab_SettingsActivity)

        val sharedFile = "com.sukacita.dailymemedigest"
        var shared : SharedPreferences = getSharedPreferences(sharedFile, Context.MODE_PRIVATE)
        val userStr = shared.getString("user", null)
        val user = getUser(userStr.toString())

        if (user.avatarUrl != "") {
            Glide.with(this).load(user.avatarUrl).into(imgProfile)
        }

        txtName.text = "${user.firstname} ${user.lastname}"
        txtDate.text = "Active since ${user.regisDate}"
        txtUsername.text = user.username

        var fnHint = "Enter First Name"
        var lnHint = "Enter Last Name (optional)"

        if (user.firstname != "User") {
            fnHint = user.firstname
        }
        if (user.lastname != "") {
            lnHint = user.lastname
        }

        txtFirstName.hint = fnHint
        txtLastName.hint = lnHint

        chkPrivacy.isChecked = user.privacySetting != 0

        btnSave.setOnClickListener() {

            if (txtFirstName.hint != "Enter First Name" || txtFirstName.text != "") {
                var privSett = 0
                if (chkPrivacy.isChecked) {
                    privSett = 1
                }

                val q = Volley.newRequestQueue(this)
                val url = "https://scheday.site/nmp/update_profile.php"

                val stringRequest = object : StringRequest(
                    Request.Method.POST, url,
                    Response.Listener<String> {
//                    Log.d("apiresult", it)
                        val obj = JSONObject(it)
                        if(obj.getString("result") == "OK") {
//                            var json = "{\"result\":\"OK\",\"data\":[{\"id\":1,\"username\":\"budiman\",\"password\":\"budiman\",\"firstname\":\"Budiman\",\"lastname\":\"Stonks\",\"regisDate\":\"2023-01-06 22:30:18\",\"avatarurl\":\"https:\\/\\/blue.kumparan.com\\/image\\/upload\\/fl_progressive,fl_lossy,c_fill,q_auto:best,w_640\\/v1612510485\\/werkeh8ecxb4lxemiru3.jpg\",\"privacysetting\":1}]}"


                            Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show()
                        }},
                    Response.ErrorListener {
                        Log.d("cekparams", it.message.toString())
                    }
                ) {
                    override fun getParams(): MutableMap<String, String> {
                        val params = HashMap<String, String>()
                        params["username"] = user.username
                        params["firstname"] = txtFirstName.text.toString()
                        params["avatarurl"] = user.avatarUrl
                        params["privacysetting"] = privSett.toString()
                        if (txtLastName.hint != "Enter Last Name (optional)" || txtLastName.text != "") {
                            params["lastname"] = txtLastName.text.toString()
                        }
                        return params
                    }
                }
                q.add(stringRequest)

                finish()
            } else {
                Toast.makeText(this, "Please fill in your First Name to continue", Toast.LENGTH_SHORT).show()
            }
        }

        fab.setOnClickListener() {
            logout(shared)
        }
    }

    private fun logout(shared: SharedPreferences) {
        var editor : SharedPreferences.Editor = shared.edit()
        editor.clear()
        editor.apply()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun getUser(userStr: String): User {
        val obj = JSONObject(userStr)
        val data = obj.getJSONArray("data")
        val userObj = data.getJSONObject(0)

        var fn = "User"
        var ln = ""

        if (userObj.getString("firstname") != "") {
            fn = userObj.getString("firstname")
        }
        if (userObj.getString("lastname") != "") {
            ln = userObj.getString("lastname")
        }

        return User(
            userObj.getInt("id"),
            userObj.getString("username"),
            fn,
            ln,
            userObj.getString("regisDate"),
            userObj.getString("avatarurl"),
            userObj.getInt("privacysetting")
        )
    }
}