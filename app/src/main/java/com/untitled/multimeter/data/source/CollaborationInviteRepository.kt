package com.untitled.multimeter.data.source

import com.untitled.multimeter.MultimeterApp.Companion.getRealmInstance
import com.untitled.multimeter.data.model.CollaborationInvite
import com.untitled.multimeter.data.model.Experiment
import com.untitled.multimeter.data.model.UserInfo

class CollaborationInviteRepository {
    val mRealm = getRealmInstance()

     suspend fun createInvitationBlocking(experiment : Experiment, receiver : UserInfo, sender: UserInfo) : Result<Unit>{
        return runCatching {
            val invite = CollaborationInvite(experiment, receiver, sender)
            mRealm.write { this.copyToRealm(invite) }
        }
    }
}