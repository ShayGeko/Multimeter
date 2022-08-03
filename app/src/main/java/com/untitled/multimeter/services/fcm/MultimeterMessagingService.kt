package com.untitled.multimeter.services.fcm

import android.app.Service
import com.untitled.multimeter.MultimeterApp

class MultimeterMessagingService() : Service(){
    val t = MultimeterApp.realmApp.currentUser
}