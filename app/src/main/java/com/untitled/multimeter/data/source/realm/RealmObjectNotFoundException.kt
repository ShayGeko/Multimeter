package com.untitled.multimeter.data.source.realm

class RealmObjectNotFoundException : Throwable{
    constructor(message: String, cause : Throwable?) : super(message, cause)
    constructor(message: String) : super(message)
    constructor() : super()
}