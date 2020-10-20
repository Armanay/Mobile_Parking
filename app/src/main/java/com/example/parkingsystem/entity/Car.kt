package com.example.parkingsystem.entity

data class Car(
    val carId: String,
    val carOwnerId: String,
    val carName: String,
    val carNo:String
) {
    constructor():this("","","","")
    override fun toString(): String {
        return "$carNo"
    }
}