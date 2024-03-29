package com.sukacita.dailymemedigest

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import jp.wasabeef.glide.transformations.BlurTransformation
import org.json.JSONObject
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    val REQUEST_UPDATE = 1
    val fragments = arrayListOf(
        HomeFragment(),
        MyCreationFragment(),
        LeaderboardFragment(),
        SettingsFragment()
    )

    var user = User(0,"", "", "", "", "", 0)
    var header: View? = null
    var loadCompleted = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawer_layout)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        // top bar
        supportActionBar?.title = "Daily Meme Digest"
        supportActionBar?.setDisplayHomeAsUpEnabled(false) // muncul burger icon

        // buat drawer sama biar bisa respond ke open/close drawernya
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        var drawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.app_name,
                R.string.app_name)
        drawerToggle.isDrawerIndicatorEnabled = true
        drawerToggle.syncState()

        // ngambil json user yg di sharedpreferences, kalo ada
        val sharedFile = "com.sukacita.dailymemedigest"
        var shared : SharedPreferences = getSharedPreferences(sharedFile, Context.MODE_PRIVATE)
        val userStr = shared.getString("user", null)
        // kalo user ga ada, buka loginactivity, mainactivity di tutup.
        if (userStr == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // ambil komponen2
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNav)
        val navView: NavigationView = findViewById(R.id.navView)

        header = navView.getHeaderView(0)

        // convert string json jadi object user
        user = getUser(userStr.toString())

        // buat refresh sharedpref (memastikan data spref sama yg ada di database)
        updateUserSpref(user.username)

        // karena method diatas manggil webservice, jalan di thread sendiri, ada kemungkinan data belom sampek,
        // maka thread main di stop dulu buat nunggu data biar sampek. kalok data belom sampek, nanti keluar putih.
        Thread.sleep(500)
        updateUser()
        // user terbaru di set ke global, biar datanya bisa dipake sama activity/fragment/adapter lain.
        Global.currentUser = user

        // ngambil meme2 dan leaderboard dari db
        getHomeMemes()
        getUserMemes(user.id)
        getLeaderboard()
        Thread.sleep(1500)

        viewPager.adapter = MyViewPagerAdapter(this, fragments)
        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                // item bottomnav diganti ngikut posisi viewpager
                bottomNav.selectedItemId = bottomNav.menu[position].itemId
            }
        })


        bottomNav.setOnItemSelectedListener {
            updateMenuSelected(when(it.itemId) {
                R.id.itemHome -> 0
                R.id.itemMyCreation -> 1
                R.id.itemLeaderboard -> 2
                R.id.itemSettings -> settings(bottomNav, navView)
                else -> 0
            }, viewPager, navView)

            true
        }

        navView.setNavigationItemSelectedListener {
            updateMenuSelected(when(it.itemId) {
                R.id.itemHome -> 0
                R.id.itemMyCreation -> 1
                R.id.itemLeaderboard -> 2
                R.id.itemSettings -> settings(bottomNav, navView)
                else -> 0
            }, viewPager, navView)

            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        val txtNameHeader: TextView = header!!.findViewById(R.id.txtName_drawerHeader)
        val txtUsername: TextView = header!!.findViewById(R.id.txtUsername_drawerHeader)
        val fabHeader: FloatingActionButton = header!!.findViewById(R.id.fabHeader)
        val imgProfilePic: ImageView = header!!.findViewById(R.id.imgProfilePic_drawerHeader)
        val imgBg: ImageView = header!!.findViewById(R.id.imgBg_drawerHeader)

        txtNameHeader.text = "${user.firstname} ${user.lastname}"
        txtUsername.text = user.username

//        val random = Random.nextInt(0, 9)
        val bgUrl = "https://media.timeout.com/images/105659619/750/422/image.jpg"
//        val bgUrl = Global.homeMemes[random].imageurl
        Glide.with(this).load(bgUrl).apply(
            RequestOptions.bitmapTransform(BlurTransformation(15, 2))).into(imgBg)

        if (user.avatarUrl != "") {
            Glide.with(this).load(user.avatarUrl).into(imgProfilePic)
        } else {
            val defaultPfp = "https://www.personality-insights.com/wp-content/uploads/2017/12/default-profile-pic-e1513291410505.jpg"
            Glide.with(this).load(defaultPfp).into(imgProfilePic)
        }

        fabHeader.setOnClickListener() {
            logout(shared)
        }

        loadCompleted = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_UPDATE) {
                updateUser()
                updateHeader()
            }
        }
    }

    // buat refresh habis komen
    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    // update menu yg di select (di header, bottomnav, viewpager)
    private fun updateMenuSelected(id: Int, viewPager: ViewPager2, navView: NavigationView) {
        viewPager.currentItem = id
        navView.menu.getItem(id).isChecked = true
        navView.menu.getItem(id).isCheckable = true
    }

    private fun settings(bottomNav: BottomNavigationView, navView: NavigationView): Int {
        for (i in 0 until 3) {
            navView.menu.getItem(i).isChecked = false
            navView.menu.getItem(i).isCheckable = false
        }

        val intent = Intent(this, SettingsActivity::class.java)
        startActivityForResult(intent, REQUEST_UPDATE)

        return 0
    }

    private fun logout(shared: SharedPreferences) {
        var editor : SharedPreferences.Editor = shared.edit()
        editor.clear()
        editor.apply()
        Global.homeMemes.clear()
        Global.userMemes.clear()
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

    override fun onResume() {
        super.onResume()
        
    }

    private fun updateUserSpref(username: String) {
        val q = Volley.newRequestQueue(this)
        val url = "https://scheday.site/nmp/get_user.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> {
                val obj = JSONObject(it)
                if(obj.getString("result") == "OK") {
                    val sharedFile = "com.sukacita.dailymemedigest"
                    var shared : SharedPreferences = getSharedPreferences(sharedFile, Context.MODE_PRIVATE)
                    val userStr = shared.getString("user", null)

                    val editor = shared.edit()
                    editor.clear()
                    editor.putString("user", it)
                    editor.apply()
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

    private fun updateUser() {
        val sharedFile = "com.sukacita.dailymemedigest"
        var shared : SharedPreferences = getSharedPreferences(sharedFile, Context.MODE_PRIVATE)
        val userStr = shared.getString("user", null)
        user = getUser(userStr.toString())
    }

    fun updateHeader() {
        val txtNameHeader: TextView = header!!.findViewById(R.id.txtName_drawerHeader)
        txtNameHeader.text = "${user.firstname} ${user.lastname}"
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    private fun getHomeMemes() {
        Global.homeMemes.clear()
        val q = Volley.newRequestQueue(this)
        val url = "https://scheday.site/nmp/get_home_memes.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> {
                    Log.d("LOG_MEME_HOME", it)
                val obj = JSONObject(it)
                if(obj.getString("result") == "OK") {
                    val data = obj.getJSONArray("data")
//                    Log.d("datalen", data.length().toString())
                    for(i in 0 until data.length()) {
                        val memeObj = data.getJSONObject(i)
                        val meme = Meme(
                            memeObj.getInt("id"),
                            memeObj.getString("imageurl"),
                            memeObj.getString("toptext"),
                            memeObj.getString("bottomtext"),
                            memeObj.getInt("numoflikes"),
                            memeObj.getInt("users_id"),
                            0,
                            memeObj.getInt("commentcount"),
                            memeObj.getString("date"),
                            memeObj.getInt("isLiked")
                        )
                        Global.homeMemes.add(meme)
                    }
                } else {
                    Toast.makeText(this, "Invalid credentials. Please check your username and password", Toast.LENGTH_SHORT).show()
                }},
            Response.ErrorListener {
                Log.d("cekparams", it.message.toString())
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["userid"] = Global.currentUser.id.toString()
                return params
            }
        }
        q.add(stringRequest)
    }

    private fun getUserMemes(id: Int) {
        Global.userMemes.clear()
        val q = Volley.newRequestQueue(this)
        val url = "https://scheday.site/nmp/get_user_memes.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> {
                Log.d("apiresult", it)
                val obj = JSONObject(it)
                if(obj.getString("result") == "OK") {
                    val data = obj.getJSONArray("data")
                    Log.d("datalen", data.length().toString())
                    for(i in 0 until data.length()) {
                        val memeObj = data.getJSONObject(i)
                        val meme = Meme(
                            memeObj.getInt("id"),
                            memeObj.getString("imageurl"),
                            memeObj.getString("toptext"),
                            memeObj.getString("bottomtext"),
                            memeObj.getInt("numoflikes"),
                            memeObj.getInt("users_id"),
                            0,
                            memeObj.getInt("commentcount"),
                            memeObj.getString("date"),
                            0
                        )
                        Log.d("objparams", memeObj.getString("toptext"))
                        Global.userMemes.add(meme)
                    }
                }},
            Response.ErrorListener {
                Log.d("cekparams", it.message.toString())
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["userid"] = id.toString()
                return params
            }
        }
        q.add(stringRequest)
    }

    private fun getLeaderboard() {
        Global.leaderboardArr.clear()
        val q = Volley.newRequestQueue(this)
        val url = "https://scheday.site/nmp/get_leaderboard.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> {
                val obj = JSONObject(it)
                if(obj.getString("result") == "OK") {
                    val data = obj.getJSONArray("data")
                    for(i in 0 until data.length()) {
                        val leaderboardobj = data.getJSONObject(i)
                        val lb = Leaderboard(
                            leaderboardobj.getString("avatarurl"),
                            leaderboardobj.getString("firstname"),
                            leaderboardobj.getString("lastname"),
                            leaderboardobj.getInt("sum"),
                            leaderboardobj.getInt("privacysetting")
                        )
                        Global.leaderboardArr.add(lb)
                    }
                }},
            Response.ErrorListener {
                Log.d("cekparams", it.message.toString())
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                return params
            }
        }
        q.add(stringRequest)
    }

}