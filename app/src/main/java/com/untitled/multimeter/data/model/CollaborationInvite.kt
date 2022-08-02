package com.untitled.multimeter.data.model

import io.realm.kotlin.mongodb.User
import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class CollaborationInvite(
    val experiment : Experiment,
    val receiver : UserInfo,
    val sender : UserInfo
) : RealmObject {
    @PrimaryKey
    val _id : ObjectId = ObjectId.create()
}