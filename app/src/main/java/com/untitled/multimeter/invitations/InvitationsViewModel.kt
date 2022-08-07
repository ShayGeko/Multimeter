package com.untitled.multimeter.invitations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.untitled.multimeter.data.model.CollaborationInvite
import com.untitled.multimeter.data.model.Experiment
import com.untitled.multimeter.data.model.UserInfo
import com.untitled.multimeter.data.source.CollaborationInviteRepository
import com.untitled.multimeter.data.source.ExperimentRepository
import com.untitled.multimeter.data.source.UserRepository
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.types.ObjectId

class InvitationsViewModel(
    private val userRepository: UserRepository,
    private val experimentRepository: ExperimentRepository,
    private val collaborationInviteRepository: CollaborationInviteRepository
) : ViewModel() {

    /**
     * Attempts to register the user with the specified email and password
     *
     * @returns
     * LiveData of the invitations for the user
     */
    fun changesToUsersInvitations() : LiveData<ResultsChange<CollaborationInvite>> {
        return collaborationInviteRepository.observeChangesToUsersInvitations()
    }

    fun accept(invite: CollaborationInvite){
        collaborationInviteRepository.deleteInvitation(invite)
    }
    fun decline(invite: CollaborationInvite){
        collaborationInviteRepository.deleteInvitation(invite)
    }
}