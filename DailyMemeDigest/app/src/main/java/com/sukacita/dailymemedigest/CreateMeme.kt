package com.sukacita.dailymemedigest

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.widget.addTextChangedListener
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import org.json.JSONObject

class CreateMeme : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_meme)

        val sharedFile = "com.sukacita.dailymemedigest"
        var shared : SharedPreferences = getSharedPreferences(sharedFile, Context.MODE_PRIVATE)
        val userStr = shared.getString("user", null)
        val user = getUser(userStr.toString())

        val urlImg = findViewById<ImageView>(R.id.imgMeme_Create)
        val toptext = findViewById<TextView>(R.id.txtTopFill)
        val bottomtext = findViewById<TextView>(R.id.txtBottomFill)
        val txtUrl = findViewById<EditText>(R.id.txtImageUrl)
        val txtTop = findViewById<EditText>(R.id.txtTop)
        val txtBottom = findViewById<EditText>(R.id.txtBottom)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        txtUrl.addTextChangedListener() {
            Glide.with(this).load(txtUrl.text.toString()).into(urlImg)
        }
        txtTop.addTextChangedListener() {
            toptext.text = txtTop.text
        }
        txtBottom.addTextChangedListener() {
            bottomtext.text = txtBottom.text
        }
        btnSubmit.setOnClickListener() {
            val q = Volley.newRequestQueue(this)
            val url = "https://scheday.site/nmp/add_meme.php"

            val stringRequest = object : StringRequest(
                Request.Method.POST, url,
                Response.Listener<String> {
//                    Log.d("apiresult", it)
                    val obj = JSONObject(it)
                    if(obj.getString("result") == "OK") {
                        Toast.makeText(this, "Create meme success", Toast.LENGTH_SHORT).show()
                        setResult(Activity.RESULT_OK)
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
                    params["userid"] = user.id.toString()
                    params["imageurl"] = txtUrl.text.toString()
                    params["toptext"] = txtTop.text.toString()
                    params["bottomtext"] = txtBottom.text.toString()
                    return params
                }
            }
            q.add(stringRequest)
        }
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
        if (userObj.getString("lastname") != "null") {
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