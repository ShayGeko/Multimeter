package com.untitled.multimeter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.untitled.multimeter.connection.ConnectionFragment
import com.untitled.multimeter.empty.Empty
import com.untitled.multimeter.experiments.ExperimentsFragment
import com.untitled.multimeter.invitations.InvitationsFragment
import com.untitled.multimeter.login.LoginActivity
import com.untitled.multimeter.settings.SettingsFragment

class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu)

        // get the application user
        val user = MultimeterApp.realmApp.currentUser
        // if no user, or if login expired, prompt login
        if(user == null || !user.loggedIn){
            startActivity(Intent(this, LoginActivity::class.java))
            finish();
        }
        else {
            Log.d(MultimeterApp.APPLICATION_TAG, user.toString())
        }
        //Get xml views
        val tabLayout = findViewById<TabLayout>(R.id.main_menu_tablayout)
        val viewPager2 = findViewById<ViewPager2>(R.id.view_pager2)

        //Add fragments to viewpager2
        val fragmentList = listOf(ExperimentsFragment(), MainFragment(), InvitationsFragment(), SettingsFragment())

        val viewPagerAdapter =
            ViewPagerAdapter(fragmentList, this.supportFragmentManager, lifecycle)
        viewPager2.adapter = viewPagerAdapter

        //Create TabLayoutMediator (link tabs and viewpager for navigation)
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            if(position == 0) {
                tab.text="Experiments"
            }
            if(position == 1) {
                tab.text="Measure"
            }
            if(position == 2) {
                tab.text="Invites"
            }
            if(position == 3) {
                tab.text="Settings"
            }
        }.attach()
    }
}
