package com.untitled.multimeter.data.model

import io.realm.kotlin.types.ObjectId
import java.io.Serializable

/**
 * Measurement Info
 */
data class MeasurementDataPointModel(
    var _id : ObjectId = ObjectId.create(),

    var x: Double = 0.0,
    var y: Double = 0.0
): Serializable {}