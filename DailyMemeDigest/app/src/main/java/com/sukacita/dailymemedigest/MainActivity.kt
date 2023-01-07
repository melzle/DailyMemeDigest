package com.sukacita.dailymemedigest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

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


        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNav)
        val navView: NavigationView = findViewById(R.id.navView)

        viewPager.adapter = MyViewPagerAdapter(this, fragments)
        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                bottomNav.selectedItemId = bottomNav.menu[position].itemId
            }
        })

        bottomNav.setOnItemSelectedListener {
            viewPager.currentItem = when(it.itemId) {
                R.id.itemHome -> 0
                R.id.itemMyCreation -> 1
                R.id.itemLeaderboard -> 2
                R.id.itemSettings -> settings(bottomNav)
                else -> 0
            }

            true
        }

        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.itemHome -> Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                R.id.itemMyCreation -> Toast.makeText(this, "Creation", Toast.LENGTH_SHORT).show()
                R.id.itemLeaderboard -> Toast.makeText(this, "Leaderboard", Toast.LENGTH_SHORT).show()
                R.id.itemSettings -> Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true

        }

//        navView.setOnClickListener()

        val fab: FloatingActionButton? = findViewById(R.id.floatingActionButton)
        fab?.setOnClickListener() {
            Toast.makeText(this, "halo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun settings(bottomNav: BottomNavigationView): Int {
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
//        bottomNav.selectedItemId = bottomNav.menu[0].itemId
        return 0
    }
}