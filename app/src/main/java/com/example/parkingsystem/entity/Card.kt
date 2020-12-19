package com.example.parkingsystem.entity

data class Card(
    val cardNum: String,
    val cvc: String,
    val cardOwner: String,
    val expDate: String
){
    constructor():this("","","","")
}