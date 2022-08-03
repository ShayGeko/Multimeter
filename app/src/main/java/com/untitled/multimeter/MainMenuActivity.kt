package com.untitled.multimeter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
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
                tab.setIcon(R.drawable.ic_experiments)
            }
            if(position == 1) {
                tab.setIcon(R.drawable.ic_measure)
            }
            if(position == 2) {
                tab.setIcon(R.drawable.ic_invite)
            }
            if(position == 3) {
                tab.setIcon(R.drawable.ic_settings)
            }
        }.attach()
    }
}
