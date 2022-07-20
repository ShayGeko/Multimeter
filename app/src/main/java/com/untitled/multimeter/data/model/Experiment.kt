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