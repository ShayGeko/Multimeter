package com.untitled.multimeter.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.untitled.multimeter.MultimeterApp.Companion.getRealmInstance
import com.untitled.multimeter.MultimeterApp.Companion.realmApp
import com.untitled.multimeter.data.model.CollaborationInvite
import com.untitled.multimeter.data.model.Experiment
import com.untitled.multimeter.data.model.UserInfo
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.ObjectId
import kotlinx.coroutines.*

class CollaborationInviteRepository {
    val mRealm = getRealmInstance()

     suspend fun createInvitationBlocking(experiment : Experiment, receiver : UserInfo, sender: UserInfo) : Result<Unit>{
        return runCatching {
            val invite = CollaborationInvite(experiment, receiver, sender)
            mRealm.write { this.copyToRealm(invite) }
        }
    }

    /**
     * get the invitations for the current user -
     * **user needs to be logged in**
     */
    fun observeChangesToUsersInvitations() : LiveData<ResultsChange<CollaborationInvite>>{
        val userId = ObjectId.from(realmApp.currentUser!!.identity)

        val changes = MutableLiveData<ResultsChange<CollaborationInvite>>()

        CoroutineScope(Dispatchers.IO).launch {
            val invitesFlow =
                mRealm.query<CollaborationInvite>("receiver._id == $0", userId).find().asFlow()

            async{
                invitesFlow.collect { results -> changes.postValue(results)
                }
            }
        }

        return changes
    }

    fun deleteInvitation(invite: CollaborationInvite){
        CoroutineScope(Dispatchers.IO).launch {
            mRealm.writeBlocking {
                val existingInvite = findLatest(invite)

                if (existingInvite != null) {
                    delete(existingInvite)
                }
            }
        }
    }
}