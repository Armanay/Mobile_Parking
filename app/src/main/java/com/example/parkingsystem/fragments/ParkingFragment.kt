package com.example.parkingsystem.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.parkingsystem.FirebaseConst
import com.example.parkingsystem.R
import com.example.parkingsystem.binder.ParkingDetailBinder
import com.example.parkingsystem.entity.Car
import com.example.parkingsystem.entity.Parking
import com.example.parkingsystem.entity.ParkingSpace
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_parking.*
import kotlinx.android.synthetic.main.fragment_parking.view.*

class ParkingFragment : Fragment(), ParkingDetailBinder {

    companion object{
        const val PARKING = "parking"
        const val SPACE_OBJ = "space_obj"
    }

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }
    lateinit var homeFragment: HomeFragment

    var textView_msg: TextView? = null
    var textView_msg_space: TextView? = null
    var textView_msg_car: TextView? = null
    var chosenParking: Parking? = null
    var chosenParkingSpace: ParkingSpace? = null
    var userCar: Car? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_parking, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = "Парковка"

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        textView_msg = this.select_parking
        textView_msg_car = this.select_car
        textView_msg_space = this.select_space
        loadParkingData(view)
        view.take_btn_place.setOnClickListener {

            when {
                chosenParkingSpace == null -> {
                    Toast.makeText(activity?.baseContext, "Выберите место", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                userCar == null -> {
                    Toast.makeText(activity?.baseContext, "Выберите место", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                chosenParking == null -> {
                    Toast.makeText(activity?.baseContext, "Выберите парковку", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                else -> {
                    getParkingSpace(auth.currentUser!!.uid, parkingSpace = chosenParkingSpace!!, car = userCar!!)
                }
            }
        }

        super.onViewCreated(view, savedInstanceState)

    }


    private fun loadParkingData(view: View) {
        val chooseCarBtn = view.findViewById<Button>(R.id.choose_car)
        val chooseParkingBtn = view.findViewById<Button>(R.id.choose_parking)
        val choosePlaceBtn = view.findViewById<Button>(R.id.choose_place)

        chooseParkingBtn.setOnClickListener {
            val bottomSheetDialog = ParkingListFragment()
            bottomSheetDialog.setTargetFragment(this, 1)
            bottomSheetDialog.show(requireActivity().supportFragmentManager, "ParkingList")
        }
        chooseCarBtn.setOnClickListener {
            val bottomSheetDialog = CarListFragment()
            bottomSheetDialog.setTargetFragment(this, 2)
            bottomSheetDialog.show(requireActivity().supportFragmentManager, "CarList")
        }

        choosePlaceBtn.setOnClickListener {
            val bottomSheetDialog = PlaceListFragment()
            if (chosenParking != null){
                val args = Bundle()
                args.putString(PARKING, chosenParking!!.parkingName)
                bottomSheetDialog.arguments = args
            }
            bottomSheetDialog.setTargetFragment(this, 3)
            bottomSheetDialog.show(requireActivity().supportFragmentManager, "PlaceList")
        }
    }


    private fun getParkingSpace(uid: String, parkingSpace: ParkingSpace, car: Car) {
        db.collection(FirebaseConst.USER_COLLECTION)
                .document(uid)
                .update(
                    mapOf(
                        "userSpace" to parkingSpace,
                        "selectedCar" to car
                    )
                )
                .addOnSuccessListener {
                    updateSpace(parkingSpace)
                }
                .addOnFailureListener {
                    Log.d("errrrror", "Can not take place")
                }

    }

    private fun updateSpace(space: ParkingSpace) {
        val handler = Handler()
        val spaceCol = db.collection(FirebaseConst.PARKING_SPACE_COLLECTION)
            .document(space.parkingSpaceId)
        spaceCol.update("free", false)
            .addOnSuccessListener {
                Log.d("uraaaaa", "updated")
                Toast.makeText(activity?.baseContext, "Success", Toast.LENGTH_LONG).show()
                handler.postDelayed(
                    {
                        val homeFragment = HomeFragment()
                        val bundle = Bundle()
                        bundle.putSerializable(SPACE_OBJ, space.parkingSpaceId)
                        homeFragment.arguments = bundle
                        activity?.supportFragmentManager
                            ?.beginTransaction()?.replace(R.id.frame_layout, homeFragment)
                            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)?.commit()
                    }
                    , 3000)
            }
            .addOnFailureListener {
                Log.d("neeeet", it.localizedMessage)
            }

    }

    override fun chosenParking(parking: Parking) {
        chosenParking = parking
        view?.choose_parking?.text = parking.parkingName
    }

    @SuppressLint("SetTextI18n")
    override fun chosenPlace(parkingSpace: ParkingSpace) {
        chosenParkingSpace = parkingSpace
        view?.choose_place?.text = "${parkingSpace.spaceSection}, ${parkingSpace.parkingNo}"

    }

    override fun chosenCar(chosenCar: Car) {
        userCar = chosenCar
        view?.choose_car?.text = chosenCar.carNo
    }

}
