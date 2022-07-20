package com.untitled.multimeter.settings

import androidx.lifecycle.ViewModel
import com.untitled.multimeter.MultimeterApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel() : ViewModel(){

    fun logout(){
        val user = MultimeterApp.realmApp.currentUser

        CoroutineScope(Dispatchers.IO).launch {
            user?.logOut()
        }
    }
}