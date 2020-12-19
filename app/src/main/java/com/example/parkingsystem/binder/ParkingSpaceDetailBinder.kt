package com.example.simplechatapp.communcators

import com.example.parkingsystem.entity.Parking

interface ParkingSpaceDetailBinder {
    fun chosenParkingSpace(parking: Parking)
}