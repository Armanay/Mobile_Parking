package com.example.parkingsystem.binder

import com.example.parkingsystem.entity.Car
import com.example.parkingsystem.entity.Parking
import com.example.parkingsystem.entity.ParkingSpace

interface ParkingDetailBinder {
    fun chosenParking(parking: Parking)
    fun chosenPlace(parkingSpace: ParkingSpace)
    fun chosenCar(chosenCar: Car)
}