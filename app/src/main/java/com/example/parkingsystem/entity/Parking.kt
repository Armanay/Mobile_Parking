package com.example.parkingsystem.entity

data class Parking(
    val address: String,
    val parkingId: String,
    val parkingSpaceQuantity: Int,
    val parkingName: String

) {
    constructor():this("","",0,"")
}