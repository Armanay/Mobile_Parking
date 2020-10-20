package com.example.parkingsystem.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.parkingsystem.FirebaseConst
import com.example.parkingsystem.R
import com.example.parkingsystem.entity.ParkingSpace
import com.example.parkingsystem.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlin.time.ExperimentalTime


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {
    private val handler = Handler()
    private var millisInFuture: Long = 0
    private lateinit var parkingName: TextView
    private lateinit var carSelec: TextView
    private lateinit var spaceSelec:TextView
    private lateinit var counterText:TextView
    private val db by lazy{ FirebaseFirestore.getInstance()}
    private val auth by lazy{ FirebaseAuth.getInstance()}
    @ExperimentalTime
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =  inflater.inflate(R.layout.fragment_home, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = "Детали парковки"
        view.cancel_take_btn.setOnClickListener {
            endReserve(auth.currentUser!!.uid)
        }
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        millisInFuture = 100000
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkUser(auth.currentUser!!.uid)
        parkingName = view.findViewById(R.id.parking_name)
        spaceSelec = view.findViewById(R.id.place_section)
        carSelec = view.findViewById(R.id.car_no_sel)
        counterText = view.findViewById(R.id.counter_time)
    }

    override fun onResume() {
        super.onResume()
        checkUser(auth.currentUser!!.uid)
    }

    private fun checkUser(uid: String){
        db.collection(FirebaseConst.USER_COLLECTION)
            .document(uid)
            .addSnapshotListener{ snapshot, e ->
                if (e != null){
                    Log.d("eerrrror", e.localizedMessage)
                    return@addSnapshotListener
                }
                val user = snapshot?.toObject(User::class.java)
                if (user != null){
                    if (user.selectedCar.carName.isNotBlank() && user.selectedCar.carNo.isNotBlank() && user.userSpace.spaceSection.isNotBlank()){
                        timerReserve()
                        userParkingSpace()
                    }
                }
            }

    }

   private fun timerReserve(){
        val timer = object: CountDownTimer(millisInFuture, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / (1000 * 60) % 60
                val seconds = (millisUntilFinished / 1000) % 60
                counterText.text = "${minutes}:${seconds}"
                millisInFuture = millisUntilFinished
                }

            override fun onFinish() {
                if (activity != null){
                    val alert: AlertDialog.Builder = AlertDialog.Builder(activity)
                    alert.setTitle("Your reservation time is out!")
                    // alert.setMessage("Message");

                    // alert.setMessage("Message");
                    alert.setPositiveButton("Ok",
                        DialogInterface.OnClickListener { dialog, whichButton ->
                            endReserve(auth.currentUser!!.uid)
                        })

                    alert.show()
                }
            }
        }
        timer.start()
    }
    private fun userParkingSpace(){
        db.collection(FirebaseConst.USER_COLLECTION)
            .document(auth.currentUser!!.uid)
            .addSnapshotListener{snapshot, e ->
                if (e != null){
                    Log.d("eerrrror", e.localizedMessage)
                    return@addSnapshotListener
                }
                val user = snapshot?.toObject(User::class.java)
                if (user != null){
                    val parkingSpace = user.userSpace
                    val car = user.selectedCar
                    spaceSelec.text = parkingSpace.toString()
                    carSelec.text = car.carNo
                    parkingName.text = parkingSpace.parkingName
                }
            }
    }
    private fun endReserve(uid: String){

            db.collection(FirebaseConst.USER_COLLECTION)
                .document(uid)
                .addSnapshotListener{snapshot, e->
                    if (e != null){
                        Log.d("taaag", e.localizedMessage)
                        return@addSnapshotListener
                    }

                    val user = snapshot?.toObject(User::class.java)!!
                    updateParkingSpace(user.userSpace)
                    updateUser(user)


        }
    }
    private fun updateParkingSpace(space: ParkingSpace){
        val spaceCol = db.collection(FirebaseConst.PARKING_SPACE_COLLECTION)
            .document(space.parkingSpaceId)

        spaceCol.update("free", true)
            .addOnSuccessListener{
                Log.d("uraaaaa", "updated")
            }
            .addOnFailureListener {
                Log.d("neeeet",it.localizedMessage)
            }
    }
    private fun updateUser(user: User){
        db.collection(FirebaseConst.USER_COLLECTION)
            .document(user.uid)
            .update(mapOf(
                "userSpace.parkingId" to "" ,
                "userSpace.free" to true ,
                "userSpace.parkingSpaceId" to "" ,
                "userSpace.parkingNo" to 0 ,
                "userSpace.parkingName" to "" ,
                "userSpace.spaceSection" to "" ,
                "selectedCar.carId" to "",
                "selectedCar.carOwnerId" to "",
                "selectedCar.carName" to "",
                "selectedCar.carNo" to ""
            ))
            .addOnSuccessListener {
                Log.d("taaag", "otmena")
            }
            .addOnFailureListener {
                Log.d("taaag", it.localizedMessage)
            }
    }
}
