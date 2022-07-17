package com.untitled.multimeter

import com.jjoe64.graphview.series.DataPoint
import java.util.*
import kotlin.collections.ArrayList

data class MyData(
    val title: String,
    val date: Calendar,
    val comment: String,
    val collaborators: ArrayList<String>,
    val dataValues: ArrayList<ArrayList<DataPoint>>)