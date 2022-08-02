package com.untitled.multimeter.data.model

import com.untitled.multimeter.MultimeterApp
import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.*

/**
 * Information about the user
 */
class UserInfo() : RealmObject {
    @PrimaryKey
    var _id : ObjectId = ObjectId.create()
    
    var _partition : String = MultimeterApp.REALM_PARTITION
    var userName : String = ""
    var email : String = ""
    var experiments : RealmList<ObjectId> = RealmListObjectId(ArrayList<ObjectId>())
}