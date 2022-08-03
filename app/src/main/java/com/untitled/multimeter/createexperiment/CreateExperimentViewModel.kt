package com.untitled.multimeter.createexperiment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.untitled.multimeter.MultimeterApp
import com.untitled.multimeter.MultimeterApp.Companion.APPLICATION_TAG
import com.untitled.multimeter.data.model.Experiment
import com.untitled.multimeter.data.model.UserInfo
import com.untitled.multimeter.data.source.CollaborationInviteRepository
import com.untitled.multimeter.data.source.ExperimentRepository
import com.untitled.multimeter.data.source.UserRepository
import kotlinx.coroutines.*

class CreateExperimentViewModel(
    private val userRepository: UserRepository,
    private val experimentRepository: ExperimentRepository,
    private val collaborationInviteRepository: CollaborationInviteRepository
    ) : ViewModel() {


    val invitationReceivers : ArrayList<UserInfo> = arrayListOf()

    /**
     * Attempts to insert experiment into database
     *
     * @param experiment - Experiment
     * @returns
     *
     */
    fun insertExperiment(experiment: Experiment): LiveData<Result<Unit>> {
        Log.d(MultimeterApp.APPLICATION_TAG, "insertExperiment launched")
        val liveData = MutableLiveData<Result<Unit>>()
        CoroutineScope(Dispatchers.IO).launch {
            val result = runCatching {

                val sender = userRepository.getCurrentUserInfoBlocking()

                withContext(Dispatchers.Default) {
                    val a = experimentRepository.insertExperimentAsync(
                        experiment,
                        sender,
                        invitationReceivers
                    )

                    Log.d(MultimeterApp.APPLICATION_TAG, "inserted experiment")
                    return@withContext a
                }

            }
                .onSuccess {
                    Log.d(MultimeterApp.APPLICATION_TAG, "Insertion successful")
                    liveData.postValue(Result.success(it))
                }.onFailure {
                    Log.d(MultimeterApp.APPLICATION_TAG, "Insertion failed")
                    Log.e(APPLICATION_TAG, it.message.toString())
                    liveData.postValue(Result.failure(it))
                }

        }
        return liveData
    }

    /**
     * Add Experiment to the user
     *
     * @param experiment - Experiment
     * @returns
     *
     */
    fun addExperimentToUser(experiment: Experiment): LiveData<Result<Boolean>> {
        return userRepository.addExperimentToUser(experiment)
    }

    fun inviteCollaborator(receiverEmail: String, experiment: Experiment): LiveData<Result<UserInfo>>{
        val result = MutableLiveData<Result<UserInfo>>()
        CoroutineScope(Dispatchers.IO).launch {
            val invitationResult = runCatching {

                val receiverResult = async { userRepository.getUserInfoByEmailAsync(receiverEmail)}
                val senderResult = async {  userRepository.getCurrentUserInfoAsync()}


                val users = awaitAll(receiverResult, senderResult)

                collaborationInviteRepository.createInvitationBlocking(
                    experiment,
                    users[0],
                    users[1]
                )

                return@runCatching users[0]
            }

            result.postValue(invitationResult)
        }
        return result
    }
    fun findUser(receiverEmail: String) : LiveData<Result<UserInfo>>{
        return userRepository.getUserInfoByEmail(receiverEmail)
    }
}