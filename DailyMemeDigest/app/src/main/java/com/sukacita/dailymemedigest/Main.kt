package com.sukacita.dailymemedigest

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Bitmap
import android.widget.TextView
import android.os.Bundle
import com.sukacita.dailymemedigest.R
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.content.Intent
import android.app.Activity
import android.provider.MediaStore
import android.widget.Toast
import android.annotation.SuppressLint
import android.Manifest
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.sukacita.dailymemedigest.VolleyMultipart
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Request.Method.POST
import com.android.volley.Response
import org.json.JSONObject
import org.json.JSONException
import com.android.volley.VolleyError
import com.sukacita.dailymemedigest.VolleyMultipart.DataPart
import com.android.volley.toolbox.Volley
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.reflect.Method
import java.util.HashMap

class Main : AppCompatActivity() {
    private var bitmap: Bitmap? = null
    private var filePath: String? = null
    var imageView: ImageView? = null
    var textView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //initializing views

        //adding click listener to button
//        findViewById<View>(R.id.buttonUploadImage).setOnClickListener {
//            if (ContextCompat.checkSelfPermission(
//                    applicationContext,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
//                    applicationContext,
//                    Manifest.permission.READ_EXTERNAL_STORAGE
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                if (ActivityCompat.shouldShowRequestPermissionRationale(
//                        this@MainActivity,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE
//                    ) && ActivityCompat.shouldShowRequestPermissionRationale(
//                        this@MainActivity,
//                        Manifest.permission.READ_EXTERNAL_STORAGE
//                    )
//                ) {
//                } else {
//                    ActivityCompat.requestPermissions(
//                        this@MainActivity,
//                        arrayOf(
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                            Manifest.permission.READ_EXTERNAL_STORAGE
//                        ),
//                        REQUEST_PERMISSIONS
//                    )
//                }
//            } else {
//                Log.e("Else", "Else")
//                showFileChooser()
//            }
//        }
    }

    private fun showFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val picUri = data.data
            filePath = getPath(picUri)
            if (filePath != null) {
                try {
                    textView!!.text = "File Selected"
                    Log.d("filePath", filePath.toString())
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, picUri)
                    uploadBitmap(bitmap)
                    imageView!!.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(
                    this, "no image selected",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun getPath(uri: Uri?): String {
        var cursor = contentResolver.query(uri!!, null, null, null, null)
        cursor!!.moveToFirst()
        var document_id = cursor.getString(0)
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1)
        cursor.close()
        cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null, MediaStore.Images.Media._ID + " = ? ", arrayOf(document_id), null
        )
        cursor!!.moveToFirst()
        @SuppressLint("Range") val path =
            cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
        cursor.close()
        return path
    }

    fun getFileDataFromDrawable(bitmap: Bitmap?): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    private fun uploadBitmap(bitmap: Bitmap?) {
        val volleyMultipart: VolleyMultipart = VolleyMultipart(
            Request.Method.POST, ROOT_URL,
            Response.Listener { response ->
                try {
                    val obj = JSONObject(String(response.data))
                    Toast.makeText(applicationContext, obj.getString("message"), Toast.LENGTH_SHORT)
                        .show()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
//                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
                Log.e("GotError", "" + error.message)
            })
//        {
//            override fun getByteData(): Map<String, DataPart>?{
//                val params: MutableMap<String, DataPart> = HashMap()
//                val imagename = Global.currentUser.id.toString()
//                params["image"] = DataPart("$imagename.png", getFileDataFromDrawable(bitmap))
//                return params
//            }
//        }

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipart)
    }

    companion object {
        private const val ROOT_URL = "http://seoforworld.com/api/v1/file-upload.php"
        private const val REQUEST_PERMISSIONS = 100
        private const val PICK_IMAGE_REQUEST = 1
    }
}