package com.sukacita.dailymemedigest

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso

class CreateMeme : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_meme)

        val urlImg = findViewById<ImageView>(R.id.imgMeme_Create)
        val toptext = findViewById<TextView>(R.id.txtTopFill)
        val bottomtext = findViewById<TextView>(R.id.txtBottomFill)
        val txtUrl = findViewById<EditText>(R.id.txtImageUrl)
        val txtTop = findViewById<EditText>(R.id.txtTop)
        val txtBottom = findViewById<EditText>(R.id.txtBottom)

        txtUrl.addTextChangedListener() {
            Glide.with(this).load(txtUrl.text.toString()).into(urlImg)
        }
        txtTop.addTextChangedListener() {
            toptext.text = txtTop.text
        }
        txtBottom.addTextChangedListener() {
            bottomtext.text = txtBottom.text
        }
    }
}