package com.untitled.multimeter.invitations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.untitled.multimeter.data.model.Experiment
import com.untitled.multimeter.data.model.UserInfo
import com.untitled.multimeter.data.source.ExperimentRepository
import com.untitled.multimeter.data.source.UserRepository
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.types.ObjectId

class InvitationsViewModel(private val userRepository: UserRepository, private val experimentRepository: ExperimentRepository) : ViewModel() {

    /**
     * Attempts to register the user with the specified email and password
     *
     * @param user - email
     * @returns
     * LiveData of the invitations for the user
     */
    fun getInvitations(user: User) : LiveData<Result<Unit>> {
        return userRepository.getInvitations(user) as MutableLiveData<Result<Unit>>
    }

    /**
     * returns the experiment info for the given experimentID
     *
     * @param experimentId - experiments unique ID
     * @returns
     * LiveData of info for that experiment
     */
    fun getExperiment(experimentId: ObjectId) : LiveData<Result<Unit>> {
        return experimentRepository.getExperiment(experimentId) as MutableLiveData<Result<Unit>>
    }
}