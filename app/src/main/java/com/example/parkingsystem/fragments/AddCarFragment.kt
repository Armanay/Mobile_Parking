package com.example.parkingsystem.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.parkingsystem.FirebaseConst

import com.example.parkingsystem.R
import com.example.parkingsystem.activity.ParkingActivity
import com.example.parkingsystem.entity.Car
import com.example.parkingsystem.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_add_car.*
import kotlinx.android.synthetic.main.fragment_add_car.view.*

/**
 * A simple [Fragment] subclass.
 */
class AddCarFragment : Fragment() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    private var parkingFragment = ParkingFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_add_car, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = "Добавление машины"
        addCar(view)
        return view
    }



    private fun addCar(view: View){
        view.save_car_btn.setOnClickListener {
            if (add_car_name.text!!.isEmpty() || add_car_no.text!!.isEmpty()) {
                if (add_car_name.text!!.isEmpty()) {
                    add_car_name.error = FirebaseConst.ERROR_MSG_IS_EMPTY
                }
                if (add_car_no.text!!.isEmpty()) {
                    add_car_no.error = FirebaseConst.ERROR_MSG_IS_EMPTY
                }
            } else {
                val newCar = db.collection(FirebaseConst.CAR_COLLECTION).document()
                val car = Car(
                    newCar.id, auth.currentUser!!.uid,add_car_name.text.toString(), add_car_no.text.toString()
                )
                saveCarData(auth.currentUser!!.uid,newCar.id, car)
            }
        }
    }



    private fun saveCarData(
        uid:String,
        id: String,
        car: Car
    ){
        db.collection(FirebaseConst.CAR_COLLECTION)
            .document(id)
            .set(car).addOnSuccessListener {
                Toast.makeText(activity, "Car was added", Toast.LENGTH_LONG).show()
                add_car_no.text!!.clear()
                add_car_name.text!!.clear()
            }
        updateUserCars(uid, car)
    }

    private fun updateUserCars(uid: String,car: Car){
       db.collection(FirebaseConst.USER_COLLECTION)
                    .document(uid)
                    .update("carList", FieldValue.arrayUnion(car))
                    .addOnSuccessListener {
                        Log.d("taggat","User was updated")
                    }
    }

}
