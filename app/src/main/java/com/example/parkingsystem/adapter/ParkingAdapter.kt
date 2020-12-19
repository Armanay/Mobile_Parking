package com.example.parkingsystem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingsystem.R
import com.example.parkingsystem.entity.Parking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.parking_item_view.view.*

class ParkingAdapter(
    private val parkingList: List<Parking> = listOf(),
    private val onClick:(Parking) -> Unit
):RecyclerView.Adapter<ParkingAdapter.CarViewHolder>() {
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.parking_item_view, parent, false)
        return CarViewHolder(view)
    }

    override fun getItemCount(): Int {
        return parkingList.size
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        holder.bindCar(parkingList[position])
    }


    inner class CarViewHolder(
        private val view: View
    ) : RecyclerView.ViewHolder(view) {
        fun bindCar(parking: Parking) {
            view.parking_address_label.text = parking.address
            view.parking_name_label.text = parking.parkingName

            view.setOnClickListener {
                onClick(parking)
            }
        }
    }
}