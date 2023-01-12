package com.sukacita.dailymemedigest

import android.Manifest
//import android.Manifest.permission_group.CAMERA
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
//import android.hardware.SensorPrivacyManager.Sensors.CAMERA
//import android.media.MediaRecorder.VideoSource.CAMERA
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap

class SettingsActivity : AppCompatActivity() {
    companion object {
        val UPDATE_USER = "update"
    }

    val REQUEST_IMG_CAPTURE = 2
    val REQUEST_GALLERY = 3
    final val REQUEST_ID_MULTIPLE_PERMISSIONS = 101

    @RequiresApi(Build.VERSION_CODES.O)
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
        } else {
            val defaultPfp = "https://www.personality-insights.com/wp-content/uploads/2017/12/default-profile-pic-e1513291410505.jpg"
            Glide.with(this).load(defaultPfp).into(imgProfile)
        }

        val date = getDate(user.regisDate).toInstant().atZone(ZoneId.of("VST")).toLocalDate()
        txtName.text = "${user.firstname} ${user.lastname}"
        txtDate.text = "Active since ${date.month.toString().lowercase().replaceFirstChar { it.uppercase() }} ${date.year}"
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
            if (txtFirstName.hint != "Enter First Name" || txtFirstName.text != "".toString()) {
                var privSett = 0
                if (chkPrivacy.isChecked) {
                    privSett = 1
                }

                val q = Volley.newRequestQueue(this)
                val url = "https://scheday.site/nmp/update_profile.php"

                val stringRequest = object : StringRequest(
                    Request.Method.POST, url,
                    Response.Listener<String> {
                        val obj = JSONObject(it)
                        if(obj.getString("result") == "OK") {
//                            var json = "{\"result\":\"OK\",\"data\":[{\"id\":1,\"username\":\"budiman\",\"password\":\"budiman\",\"firstname\":\"Budiman\",\"lastname\":\"Stonks\",\"regisDate\":\"2023-01-06 22:30:18\",\"avatarurl\":\"https:\\/\\/blue.kumparan.com\\/image\\/upload\\/fl_progressive,fl_lossy,c_fill,q_auto:best,w_640\\/v1612510485\\/werkeh8ecxb4lxemiru3.jpg\",\"privacysetting\":1}]}"


                            Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show()
                            updateUser(shared, user.username)

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

            } else {
                Toast.makeText(this, "Please fill in your First Name to continue", Toast.LENGTH_SHORT).show()
            }


        }

        fab.setOnClickListener() {
            logout(shared)
        }

        val fabEditPhoto: FloatingActionButton = findViewById(R.id.fabProfilePic)
        fabEditPhoto.setOnClickListener() {
//            if(ContextCompat.checkSelfPermission(this,
//                    Manifest.permission.CAMERA) !=
//                PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this,
//                    arrayOf(Manifest.permission.CAMERA), REQUEST_IMG_CAPTURE)
//            } else {
//                takePicture()
//            }
            if(checkAndRequestPermissions(this)){
                chooseImage(this);
            }
        }

        val backbtn: ImageButton = findViewById(R.id.backbtn)
        backbtn.setOnClickListener() {
            setResult(Activity.RESULT_OK)
            finish()
        }


    }

    private fun chooseImage(context: Context) {
        val optionsMenu = arrayOf<CharSequence>(
            "Take Photo",
            "Choose from Gallery",
            "Exit"
        ) // create a menuOption Array
        // create a dialog for showing the optionsMenu
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        // set the items in builder
        builder.setItems(optionsMenu) { dialogInterface, i ->
            if (optionsMenu[i] == "Take Photo") {
                // Open the camera and get the photo
                val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(takePicture, 0)
            } else if (optionsMenu[i] == "Choose from Gallery") {
                // choose from  external storage
                val pickPhoto =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickPhoto, 1)
            } else if (optionsMenu[i] == "Exit") {
                dialogInterface.dismiss()
            }
        }
        builder.show()
    }

    // Handled permission Result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    applicationContext,
                    "FlagUp Requires Access to Camara.", Toast.LENGTH_SHORT
                )
                    .show()
            } else if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    applicationContext,
                    "FlagUp Requires Access to Your Storage.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                chooseImage(this)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val imageView: ImageView = findViewById(R.id.imgProfile_SettingsActivity)
        if (resultCode != RESULT_CANCELED) {
            when (requestCode) {
                0 -> if (resultCode == RESULT_OK && data != null) {
                    val selectedImage = data.extras!!["data"] as Bitmap?
                    imageView!!.setImageBitmap(selectedImage)
                }
                1 -> if (resultCode == RESULT_OK && data != null) {
                    val selectedImage = data.data
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    if (selectedImage != null) {
                        val cursor =
                            contentResolver.query(selectedImage, filePathColumn, null, null, null)
                        if (cursor != null) {
                            cursor.moveToFirst()
                            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                            val picturePath = cursor.getString(columnIndex)
                            imageView!!.setImageBitmap(BitmapFactory.decodeFile(picturePath))
                            cursor.close()
                        }
                    }
                }
            }
        }
    }

//    companion object {
//        val REQUEST_ID_MULTIPLE_PERMISSIONS = 101

        // function to check permission
        fun checkAndRequestPermissions(context: Activity?): Boolean {
            val WExtstorePermission = ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            val cameraPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            )
            val listPermissionsNeeded: MutableList<String> = ArrayList()
            if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.CAMERA)
            }
            if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded
                    .add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(
                    context, listPermissionsNeeded
                        .toTypedArray(),
                    REQUEST_ID_MULTIPLE_PERMISSIONS
                )
                return false
            }
            return true
        }
//    }

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

    private fun updateUser(shared: SharedPreferences, username: String) {
        val q = Volley.newRequestQueue(this)
        val url = "https://scheday.site/nmp/get_user.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> {
                val obj = JSONObject(it)
                if(obj.getString("result") == "OK") {
                    val editor = shared.edit()
                    editor.clear()
                    editor.putString("user", it)
                    editor.apply()

                    val obj = JSONObject(it)
                    val data = obj.getJSONArray("data")
                    val userObj = data.getJSONObject(0)
                    Global.currentUser.firstname = userObj.getString("firstname")
                    Global.currentUser.lastname = userObj.getString("lastname")

//                    Global.currentUser.firstname = ??
//                    Toast.makeText(this, "done", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show()
                }},
            Response.ErrorListener {
                Log.d("cekparams", it.message.toString())
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["username"] = username
                return params
            }
        }
        q.add(stringRequest)
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when(requestCode) {
//            REQUEST_IMG_CAPTURE -> {
//                if(grantResults.isNotEmpty() && grantResults[0] ==
//                    PackageManager.PERMISSION_GRANTED)
//                    takePicture()
//                else
//                    Toast.makeText(this, "You must grant permission to access the camera.", Toast.LENGTH_LONG).show()
//            }
//            REQUEST_GALLERY -> {
//                if(grantResults.isNotEmpty() && grantResults[0] ==
//                    PackageManager.PERMISSION_GRANTED)
//                    openGallery()
//                else
//                    Toast.makeText(this, "You must grant permission to access the gallery.", Toast.LENGTH_LONG).show()
//            }
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == REQUEST_IMG_CAPTURE) {
//                val extras = data!!.extras
//                val imageBitmap: Bitmap = extras!!.get("data") as Bitmap
//                val imgProfile: ImageView = findViewById(R.id.imgProfile_SettingsActivity)
//                imgProfile.setImageBitmap(imageBitmap)
//            } else if (requestCode == REQUEST_GALLERY) {
//                val extras = data!!.extras
//                val imageBitmap: Bitmap = extras!!.get("data") as Bitmap
//                val imgProfile: ImageView = findViewById(R.id.imgProfile_SettingsActivity)
//                imgProfile.setImageBitmap(imageBitmap)
//            }
//        }
//    }

    private fun getDate(s: String): Date {
        val strArr = s.split(" ")
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.parse(strArr[0])
    }

    private fun takePicture() {
        val i = Intent()
        i.action = MediaStore.ACTION_IMAGE_CAPTURE
//        i.type =
        startActivityForResult(i, REQUEST_IMG_CAPTURE)
    }

    private fun openGallery() {
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, REQUEST_IMG_CAPTURE)
    }
}