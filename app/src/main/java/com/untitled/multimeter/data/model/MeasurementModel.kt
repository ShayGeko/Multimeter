package com.untitled.multimeter.data.model

import com.jjoe64.graphview.series.DataPoint
import com.untitled.multimeter.MultimeterApp
import io.realm.kotlin.types.ObjectId
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

data class MeasurementModel(
    var _id : ObjectId = ObjectId.create(),
    var user: ObjectId = ObjectId.from(MultimeterApp.realmApp.currentUser!!.identity),
    var dataPoints: ArrayList<DataPoint> = ArrayList()
): Serializable {

}