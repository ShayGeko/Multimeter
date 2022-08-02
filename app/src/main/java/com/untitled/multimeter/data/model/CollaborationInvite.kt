package com.untitled.multimeter.data.model

import io.realm.kotlin.mongodb.User
import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class CollaborationInvite() : RealmObject {
    constructor(experiment: Experiment, receiver: UserInfo, sender:UserInfo) : this() {
        this.experiment = experiment
        this.receiver = receiver
        this.sender = sender
    }
    @PrimaryKey
    var _id : ObjectId = ObjectId.create()

    lateinit var experiment : Experiment
    lateinit var receiver : UserInfo
    lateinit var sender : UserInfo
}