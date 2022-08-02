package com.untitled.multimeter.data.model

import com.jjoe64.graphview.series.DataPoint
import com.untitled.multimeter.MultimeterApp
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.*
import kotlin.collections.ArrayList

/**
 * Measurement Info
 */
class Measurement() : RealmObject {
    @PrimaryKey
    var _id : ObjectId = ObjectId.create()

    var user: ObjectId = ObjectId.from(MultimeterApp.realmApp.currentUser!!.identity)
    var dataPoints: RealmList<MeasurementDataPoint> = realmListOf()
}