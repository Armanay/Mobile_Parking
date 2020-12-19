package com.example.parkingsystem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingsystem.R
import com.example.parkingsystem.entity.ParkingSpace
import kotlinx.android.synthetic.main.place_item_view.view.*

class LeftPlaceAdapter(
    private val placeList: List<ParkingSpace> = listOf(),
    private val onClick:(ParkingSpace) -> Unit
):RecyclerView.Adapter<LeftPlaceAdapter.CarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.place_item_view, parent, false)
        return CarViewHolder(view)
    }

    override fun getItemCount(): Int {
        return placeList.size
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        holder.bindCar(placeList[position])
    }


    inner class CarViewHolder(
        private val view: View
    ):RecyclerView.ViewHolder(view){
        fun bindCar(parkingSpace: ParkingSpace){

                view.place_name_label.text = parkingSpace.spaceSection
                view.place_num_label.text = parkingSpace.parkingNo.toString()

            view.setOnClickListener {
                onClick(parkingSpace)
            }
        }
    }
}