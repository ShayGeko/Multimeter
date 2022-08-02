package com.untitled.multimeter.experiments

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.untitled.multimeter.data.model.ExperimentModel
import com.untitled.multimeter.data.source.ExperimentRepository

class ExperimentViewModel(private val repository: ExperimentRepository) : ViewModel() {

    fun getAllExperimentsForUser() : LiveData<Result<ArrayList<ExperimentModel>>> {
        return repository.getAllExperimentsForUser()
    }
}