package com.untitled.multimeter.data.model

import io.realm.kotlin.types.ObjectId
import java.util.*
import kotlin.collections.ArrayList

data class ExperimentModel(
    val id: ObjectId,
    val title: String,
    val date: Calendar,
    val comment: String,
    val collaborators: ArrayList<String>,
    var measurements: ArrayList<MeasurementModel>) {

    override fun toString(): String {
        return "id: "+id+", title: "+title+", comment: "+comment+", collaborators: "+collaborators.toString()+", measurements: "+measurements.toString()
    }
}