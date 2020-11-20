package com.example.parkingsystem.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentTransaction
import com.example.parkingsystem.FirebaseConst
import com.example.parkingsystem.R
import com.example.parkingsystem.entity.Parking
import com.example.parkingsystem.entity.ParkingSpace
import com.example.parkingsystem.entity.User
import com.example.parkingsystem.fragments.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_parking2.*
import kotlinx.android.synthetic.main.app_bar_main.*


class ParkingActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    lateinit var homeFragment: HomeFragment
    lateinit var addCarFragment: AddCarFragment
    lateinit var profileFragment: ProfileFragment
    lateinit var parkingFragment: ParkingFragment
    lateinit var policyFragment: PolicyFragment
    lateinit var contactFragment: ContactFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking2)


//        addParkings("ADK", 60, "Сатпаева, 90")
//        addParkings("Globus", 10, "Абая проспект, 109в")
//        addParkings("Mega", 100, "Розыбакиева, 247а")
//        addParkings("Moskva", 40, "8-й микрорайон, 37/1")
//        addParkings("Sputnik", 30, "Мамыр 1-й микрорайон, 8а")
        welcome()
        supportActionBar()
        userInfo(auth.currentUser!!.uid)
    }



    private fun supportActionBar(){
        setSupportActionBar(toolBar)
        val actionBar = supportActionBar
        actionBar?.title = "Парковка"

        val drawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,drawerLayout, toolBar ,(R.string.open),(R.string.close)
        ){

        }
        drawerToggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun welcome(){
        if (auth.currentUser != null){
            db.collection(FirebaseConst.USER_COLLECTION)
                .document(auth.currentUser!!.uid)
                .addSnapshotListener{ snapshot, it ->
                    if (it != null){
                        Log.d("tagtag", it.localizedMessage)
                        return@addSnapshotListener
                    }

                    val user = snapshot?.toObject(User::class.java)
                    if (user != null){
                        Toast.makeText(this, "Welcome, ${user.username}", Toast.LENGTH_LONG).show()
                    }
                }

        }
    }

    override fun onNavigationItemSelected(menu: MenuItem): Boolean {
        when(menu.itemId){
            R.id.nav_home ->{
                homeFragment = HomeFragment()
                supportFragmentManager
                    .beginTransaction().replace(R.id.frame_layout, homeFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
            }
            R.id.nav_add_car ->{
                addCarFragment = AddCarFragment()
                supportFragmentManager
                    .beginTransaction().replace(R.id.frame_layout, addCarFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
            }
            R.id.nav_profile ->{
                profileFragment = ProfileFragment()
                supportFragmentManager
                    .beginTransaction().replace(R.id.frame_layout, profileFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
            }
            R.id.nav_logout ->{
                logout()
            }
            R.id.nav_parking ->{
                parkingFragment = ParkingFragment()
                supportFragmentManager
                    .beginTransaction().replace(R.id.frame_layout, parkingFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()

            }
            R.id.nav_contac_us ->{
                contactFragment = ContactFragment()
                supportFragmentManager
                    .beginTransaction().replace(R.id.frame_layout, contactFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
            }
            R.id.nav_policy ->{
                policyFragment = PolicyFragment()
                supportFragmentManager
                .beginTransaction().replace(R.id.frame_layout, policyFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
        }

        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            super.onBackPressed()
        }

    }

    private fun logout(){
        auth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun userInfo(uid :String){
//        db.collection(FirebaseConst.USER_COLLECTION)
//            .document(uid)
//            .addSnapshotListener { snapshot, e ->
//                if (e != null) {
//                    Log.d("tagtagtag", e.localizedMessage)
//                    return@addSnapshotListener
//                }
//                val user = snapshot?.toObject(User::class.java)
//                if (user != null) {
//                    user_email.text = user.username
//                }
//            }

    }


//    ADD parkings and parking spaces
    private fun addParkings(parkingName:String, quantity: Int, address: String){
        val newParking = db.collection(FirebaseConst.PARKING_COLLECTION).document()
        val parking = Parking(
            address,newParking.id,quantity, parkingName
        )

        db.collection(FirebaseConst.PARKING_COLLECTION)
            .document(parking.parkingName)
            .set(parking)
            .addOnSuccessListener {
                Toast.makeText(this, "Parking was added", Toast.LENGTH_LONG).show()
            }
    parkingSpacesGlobus(parking.parkingId, quantity,parkingName)
    }
    private fun parkingSpacesGlobus(parkingId: String, spaceQuantity: Int,name:String){
        for (i in 1..spaceQuantity) {
            val newParkingSpace = db.collection(FirebaseConst.PARKING_SPACE_COLLECTION).document()
            if (i in 1..20) {
                val parkingSpace = ParkingSpace(
                    parkingId, true, newParkingSpace.id, i,name,"section A"
                )
                db.collection(FirebaseConst.PARKING_SPACE_COLLECTION)
                    .document(newParkingSpace.id)
                    .set(parkingSpace)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Parking was added", Toast.LENGTH_LONG).show()
                    }

            }
            else if (i in 21..40) {
                val parkingSpace = ParkingSpace(
                    parkingId, true, newParkingSpace.id, i,name,"section B"
                )
                db.collection(FirebaseConst.PARKING_SPACE_COLLECTION)
                    .document(newParkingSpace.id)
                    .set(parkingSpace)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Parking was added", Toast.LENGTH_LONG).show()
                    }
            }
            else if (i in 41..60) {
                val parkingSpace = ParkingSpace(
                    parkingId, true, newParkingSpace.id, i,name,"section C"
                )
                db.collection(FirebaseConst.PARKING_SPACE_COLLECTION)
                    .document(newParkingSpace.id)
                    .set(parkingSpace)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Parking was added", Toast.LENGTH_LONG).show()
                    }
            }
            else if (i in 61..80) {
                val parkingSpace = ParkingSpace(
                    parkingId, true, newParkingSpace.id, i,name,"section D"
                )
                db.collection(FirebaseConst.PARKING_SPACE_COLLECTION)
                    .document(newParkingSpace.id)
                    .set(parkingSpace)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Parking was added", Toast.LENGTH_LONG).show()
                    }
            }
            else if (i in 81..100) {
                val parkingSpace = ParkingSpace(
                    parkingId, true, newParkingSpace.id, i,name,"section E"
                )
                db.collection(FirebaseConst.PARKING_SPACE_COLLECTION)
                    .document(newParkingSpace.id)
                    .set(parkingSpace)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Parking was added", Toast.LENGTH_LONG).show()
                    }
            }

        }
    }

}
