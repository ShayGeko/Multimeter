package com.untitled.multimeter

import android.app.Application
import android.util.Log
import com.untitled.multimeter.data.source.UserRepository
import io.realm.kotlin.mongodb.App

class MultimeterApp : Application() {
    val userRepository by lazy {UserRepository()}

    override fun onCreate() {
        super.onCreate()

        realmApp = App.create(getString(R.string.realm_app_id))

        Log.d(APPLICATION_TAG, "Initialized the Realm App")
    }
    companion object{
        lateinit var realmApp : App
        val APPLICATION_TAG = "Multimeter"
    }
}