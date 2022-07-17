package com.untitled.multimeter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.untitled.multimeter.empty.Empty
import com.untitled.multimeter.experiments.Experiments

class MainMenu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu)

        //Get xml views
        val tabLayout = findViewById<TabLayout>(R.id.main_menu_tablayout)
        val viewPager2 = findViewById<ViewPager2>(R.id.view_pager2)

        //Add fragments to viewpager2
        val fragmentList = listOf(Experiments(), Empty(), Empty())
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
                tab.text="Settings"
            }
        }.attach()
    }
}