package com.example.parkingsystem.entity

data class Error(
    val id: String,
    val msg: String,
    val uid: String
) {
    constructor():this("","","")

}