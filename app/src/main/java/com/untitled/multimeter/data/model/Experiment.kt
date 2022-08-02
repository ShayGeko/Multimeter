/*
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
*/

package com.untitled.multimeter.data.model

import com.untitled.multimeter.MultimeterApp
import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.*


/**
 * Experiment Info
 */
class Experiment() : RealmObject {
    @PrimaryKey
    var _id : ObjectId = ObjectId.create()
    var _partition : String = MultimeterApp.REALM_PARTITION

    var title : String = ""
    var date: Calendar = Calendar.getInstance()
    var collaborators : RealmList<String> = RealmListString(ArrayList<String>())
    var comment : String = ""
    var measurements: RealmList<ObjectId> = RealmListObjectId(ArrayList<ObjectId>())
}