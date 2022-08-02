package com.untitled.multimeter.experimentdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.untitled.multimeter.data.model.ExperimentModel
import com.untitled.multimeter.data.source.ExperimentRepository
import com.untitled.multimeter.data.source.UserRepository
import io.realm.kotlin.types.ObjectId

class ExperimentDetailsViewModel(private val userRepository: UserRepository, private val experimentRepository: ExperimentRepository) : ViewModel() {

    fun deleteExperiment(objectId: ObjectId) {
        return experimentRepository.deleteExperiment(objectId)
    }

    fun removeExperimentFromUser(objectId: ObjectId) {
        return userRepository.removeExperimentFromUser(objectId)
    }
}