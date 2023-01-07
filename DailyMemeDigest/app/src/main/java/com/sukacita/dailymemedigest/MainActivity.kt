package com.sukacita.dailymemedigest

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import jp.wasabeef.glide.transformations.BlurTransformation
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    val fragments = arrayListOf(
        HomeFragment(),
        MyCreationFragment(),
        LeaderboardFragment(),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawer_layout)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Daily Meme Digest"
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        var drawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.app_name,
                R.string.app_name)
        drawerToggle.isDrawerIndicatorEnabled = true
        drawerToggle.syncState()

        val sharedFile = "com.sukacita.dailymemedigest"
        var shared : SharedPreferences = getSharedPreferences(sharedFile, Context.MODE_PRIVATE)
        val userStr = shared.getString("user", null)
        if (userStr == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        val user = getUser(userStr.toString())

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNav)
        val navView: NavigationView = findViewById(R.id.navView)

//        updateMenuSelected(0, viewPager, navView)

        viewPager.adapter = MyViewPagerAdapter(this, fragments)
        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                bottomNav.selectedItemId = bottomNav.menu[position].itemId
            }
        })

        bottomNav.setOnItemSelectedListener {
            updateMenuSelected(when(it.itemId) {
                R.id.itemHome -> 0
                R.id.itemMyCreation -> 1
                R.id.itemLeaderboard -> 2
                R.id.itemSettings -> settings(bottomNav)
                else -> 0
            }, viewPager, navView)

            true
        }

        navView.setNavigationItemSelectedListener {
            updateMenuSelected(when(it.itemId) {
                R.id.itemHome -> 0
                R.id.itemMyCreation -> 1
                R.id.itemLeaderboard -> 2
                R.id.itemSettings -> settings(bottomNav)
                else -> 0
            }, viewPager, navView)

            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        val header: View = navView.getHeaderView(0)
        val txtNameHeader: TextView = header.findViewById(R.id.txtName_drawerHeader)
        val txtUsername: TextView = header.findViewById(R.id.txtUsername_drawerHeader)
        val fabHeader: FloatingActionButton = header.findViewById(R.id.fabHeader)
        val imgProfilePic: ImageView = header.findViewById(R.id.imgProfilePic_drawerHeader)
        val imgBg: ImageView = header.findViewById(R.id.imgBg_drawerHeader)

        txtNameHeader.text = "${user.firstname} ${user.lastname}"
        txtUsername.text = user.username

        val bg_url = "https://media.timeout.com/images/105659619/750/422/image.jpg"
//        Picasso.get().load(bg_url).into(imgBg)

        Glide.with(this).load(bg_url).apply(
            RequestOptions.bitmapTransform(BlurTransformation(15, 2))).into(imgBg)

        if (user.avatarUrl != "") {
//            Picasso.get().load(user.avatarUrl).into(imgProfilePic)
            Glide.with(this).load(user.avatarUrl).into(imgProfilePic)
        }

        fabHeader.setOnClickListener() {
            logout(shared)
        }
    }

    private fun updateMenuSelected(id: Int, viewPager: ViewPager2, navView: NavigationView) {
        viewPager.currentItem = id
        navView.menu.getItem(id).isChecked = true
        navView.menu.getItem(id).isCheckable = true
    }

    private fun settings(bottomNav: BottomNavigationView): Int {
//        bottomNav.id
//        bottomNav.menu.getItem(3).isChecked = false
//        bottomNav.menu.getItem(0).isChecked = true


        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
//        finish()
//        val menu = bottomNav.menu
////        for (i in 0 .. menu.size()) {
//        val itemStg = menu.getItem(3)
//        itemStg.isChecked = false
//
//        val itemHome = menu.getItem(0)
//        itemHome.isChecked = true
//
////        }
//

//        bottomNav.menu.getItem(0).isChecked = true
        return 0
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