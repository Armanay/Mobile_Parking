package com.example.parkingsystem.entity


data class ParkingSpace(
     val parkingId: String,
     val free: Boolean,
     val parkingSpaceId: String,
     val parkingNo: Int,
     val parkingName:String,
     val spaceSection: String
) {
    constructor():this("",false,"",0, "","")

    override fun toString(): String {
        return "$parkingNo, $spaceSection"
    }
}