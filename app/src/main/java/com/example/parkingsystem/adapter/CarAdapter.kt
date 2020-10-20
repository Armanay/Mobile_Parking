package com.example.parkingsystem.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingsystem.FirebaseConst
import com.example.parkingsystem.R
import com.example.parkingsystem.entity.Car
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.layout_car_item.view.*

class CarAdapter(
    private val carList: List<Car> = listOf()
):RecyclerView.Adapter<CarAdapter.CarViewHolder>() {
    private val db by lazy{ FirebaseFirestore.getInstance()}
    private val auth by lazy{ FirebaseAuth.getInstance()}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_car_item, parent, false)
        return CarViewHolder(view)
    }

    override fun getItemCount(): Int {
        return carList.size
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        holder.bindCar(carList[position])
    }


    inner class CarViewHolder(
        private val view: View
    ):RecyclerView.ViewHolder(view){
        fun bindCar(car: Car){
            view.car_item_name.text = car.carName
            view.car_item_number.text = car.carNo
            view.delete_user_car.setOnClickListener {
                db.collection(FirebaseConst.USER_COLLECTION)
                    .document(auth.currentUser!!.uid)
                    .update("carList", FieldValue.arrayRemove(car))
                    .addOnSuccessListener {
                        Log.d("taaag", "deleted")
                    }
                    .addOnFailureListener{
                        Log.d("taaag", it.localizedMessage)
                    }

            }
        }
    }
}