package com.untitled.multimeter.data.model

import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

/**
 * Information about the user
 */
class UserInfo() : RealmObject {
    @PrimaryKey
    var id : ObjectId = ObjectId.create()

    var userName : String = ""
    var email : String = ""
}