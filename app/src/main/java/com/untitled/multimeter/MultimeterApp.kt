package com.untitled.multimeter

import android.app.Application
import android.util.Log
import com.untitled.multimeter.data.model.Experiment
import com.untitled.multimeter.data.model.Measurement
import com.untitled.multimeter.data.model.UserInfo
import com.untitled.multimeter.data.source.ExperimentRepository
import com.untitled.multimeter.data.source.UserRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.internal.platform.ensureNeverFrozen
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.sync.SyncConfiguration

class MultimeterApp : Application() {
    val userRepository by lazy {UserRepository()}
    val experimentRepository by lazy { ExperimentRepository() }


    override fun onCreate() {
        super.onCreate()


        realmApp = App.create(getString(R.string.realm_app_id))

        Log.d(APPLICATION_TAG, "Initialized the Realm App")
    }
    companion object{
        private lateinit var mRealm : Realm

        /**
         * Returns an instance of realm for this app
         */
        fun getRealmInstance() : Realm{
            if(this::mRealm.isInitialized){
                return mRealm
            }
            else{
                val config = SyncConfiguration
                    .Builder(realmApp.currentUser!!, REALM_PARTITION, schema = setOf(UserInfo::class, Experiment::class, Measurement::class))
                    .build()
                mRealm = Realm.open(config)

                return mRealm
            }
        }

        lateinit var realmApp : App
        const val APPLICATION_TAG = "Multimeter"
        const val REALM_PARTITION = "1"
    }
}