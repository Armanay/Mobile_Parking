package com.example.parkingsystem.entity

data class User(
    val uid: String,
    val username: String,
    val phone: String,
    val password: String,
    var carList: ArrayList<Car>,
    var userSpace: ParkingSpace,
    var selectedCar: Car,
    var imgUri: String? = null,
    var cardList: ArrayList<Card> = ArrayList()
){
    constructor():this("","","","", ArrayList(), ParkingSpace(),Car())
}