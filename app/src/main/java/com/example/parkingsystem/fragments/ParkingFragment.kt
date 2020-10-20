package com.example.parkingsystem.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.parkingsystem.FirebaseConst
import com.example.parkingsystem.R
import com.example.parkingsystem.entity.Car
import com.example.parkingsystem.entity.Parking
import com.example.parkingsystem.entity.ParkingSpace
import com.example.parkingsystem.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_parking.*

/**
 * A simple [Fragment] subclass.
 */
class ParkingFragment : Fragment() {

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }
    lateinit var homeFragment: HomeFragment
    var textView_msg:TextView? = null
    var textView_msg_space:TextView? = null
    var textView_msg_car:TextView? = null

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
        super.onViewCreated(view, savedInstanceState)
        loadParkingData(view)
    }
    private fun loadParkingData(view: View)
    {
        val spinnerCar = view.findViewById<Spinner>(R.id.spinner_car)
        val parkingSpinner = view.findViewById<Spinner>(R.id.spinner_parking)
        val spaceSpinner = view.findViewById<Spinner>(R.id.spinner_parking_space)
        db.collection(FirebaseConst.PARKING_COLLECTION)
            .addSnapshotListener{querySnapshot, firebaseFirestoreException ->
                val parkings = querySnapshot?.documents?.map{
                    it.toObject(Parking::class.java)
                }?: emptyList()
                val parkingNames = ArrayList<String>()
                for (name in parkings){
                    parkingNames.add(name!!.parkingName)
                }
                val parkingAdaper = ArrayAdapter(requireActivity(), R.layout.support_simple_spinner_dropdown_item, parkingNames)
                parkingSpinner.adapter = parkingAdaper
                parkingSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        TODO("Not yet implemented")

                    }

                    @SuppressLint("SetTextI18n")
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {

                        val selectedParking = parkings[position]!!
                        val freeList = ArrayList<ParkingSpace>()
                        db.collection(FirebaseConst.PARKING_SPACE_COLLECTION)
                            .whereEqualTo("parkingId", selectedParking.parkingId)
                            .addSnapshotListener{snapshot, e->
                                if (e != null){
                                    Log.d("taaaaag", e.localizedMessage)
                                    return@addSnapshotListener
                                }

                                snapshot?.documents?.forEach {
                                    val space = it.toObject(ParkingSpace::class.java)!!
                                    if (space.free) {
                                        freeList.add(space)
                                    }
                                }
                                val spaceAdapter = ArrayAdapter(activity!!, R.layout.support_simple_spinner_dropdown_item, freeList)
                                spaceSpinner.adapter = spaceAdapter
                                spaceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")

                                    }

                                    @SuppressLint("SetTextI18n")
                                    override fun onItemSelected(
                                        parent: AdapterView<*>?,
                                        view: View?,
                                        position: Int,
                                        id: Long
                                    ) {
                                        val selPace = freeList[position]

                                        loadCargData(spinnerCar,selPace)
                                        print("$selPace sssssssssss")
                                    }

                                }

                            }



                    }

                }


            }
    }
    private fun loadCargData(spinnerCar:Spinner,parkigSpace: ParkingSpace)
    {
        if (auth.currentUser != null){
            db.collection(FirebaseConst.USER_COLLECTION)
                .document(auth.currentUser!!.uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Toast.makeText(activity, error.message, Toast.LENGTH_LONG).show()
                        return@addSnapshotListener
                    }
                    val user = snapshot?.toObject(User::class.java)

                        spinnerCar.adapter = activity?.let {
                            ArrayAdapter(
                                it,
                                R.layout.support_simple_spinner_dropdown_item,
                                user!!.carList
                            )
                        }
                        spinnerCar.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    TODO("Not yet implemented")
                                }

                                @SuppressLint("SetTextI18n")
                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    val selectedCar = user!!.carList[position]

                                    getParkingSpace(
                                        auth.currentUser!!.uid,
                                        parkigSpace,
                                        selectedCar
                                    )

                                }
                            }
                    }
                }
        }


    private fun getParkingSpace(uid: String, parkingSpace: ParkingSpace, car :Car) {
        take_btn_place.setOnClickListener {
            db.collection(FirebaseConst.USER_COLLECTION)
                .document(uid)
                .update(mapOf(
                    "userSpace" to parkingSpace,
                    "selectedCar" to car
                ))
                .addOnSuccessListener {
                    updateSpace(parkingSpace)
                    Toast.makeText(activity, "Success", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Log.d("errrrror", "Can not take place")
                }

        }

    }
    private fun updateSpace(space:ParkingSpace) {
        val spaceCol = db.collection(FirebaseConst.PARKING_SPACE_COLLECTION)
            .document(space.parkingSpaceId)

        spaceCol.update("free", false)
            .addOnSuccessListener{
                Log.d("uraaaaa", "updated")
            }
            .addOnFailureListener {
                Log.d("neeeet",it.localizedMessage)
            }

            }
}
