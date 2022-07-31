package com.untitled.multimeter.data.model

import com.jjoe64.graphview.series.DataPoint
import java.util.*
import kotlin.collections.ArrayList

data class Experiment(
    val title: String,
    val date: Calendar,
    val comment: String,
    val collaborators: ArrayList<String>,
    val dataValues: ArrayList<ArrayList<DataPoint>>)

/*
package com.untitled.multimeter.data.model

import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

/**
 * Experiment Info
 */
class Experiment() : RealmObject {
    @PrimaryKey
    var experimentId : ObjectId = ObjectId.create()

    var title : String = ""
    var collaborators : ArrayList<String> = ArrayList<String>()
    var comment : String = ""
    //var measurements: ArrayList<Measurement> = ArrayList<Measurement>()
}
 */