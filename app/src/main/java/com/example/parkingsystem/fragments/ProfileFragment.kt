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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingsystem.FirebaseConst

import com.example.parkingsystem.R
import com.example.parkingsystem.activity.BankCardActivity
import com.example.parkingsystem.activity.UpdatePassword
import com.example.parkingsystem.activity.UserCarList
import com.example.parkingsystem.adapter.CarAdapter
import com.example.parkingsystem.entity.Car
import com.example.parkingsystem.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    private val db by lazy{ FirebaseFirestore.getInstance()}
    private val auth by lazy{ FirebaseAuth.getInstance()}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_profile, container, false)
        profileInfo(view)
        profileSections(view)
        (activity as AppCompatActivity).supportActionBar?.title = "Профиль"
        return profileInfo(view)
    }

    private fun profileSections(view: View){
        view.my_cars.setOnClickListener {
            val intent = Intent(activity, UserCarList::class.java)
            startActivity(intent)
        }
        view.bank_cards.setOnClickListener {
            val intent = Intent(activity, BankCardActivity::class.java)
            startActivity(intent)
        }
        view.update_pswd.setOnClickListener {
            val intent = Intent(activity, UpdatePassword::class.java)
            startActivity(intent)
        }
    }

    private fun profileInfo(view: View): View{
        if (auth.currentUser != null){
            db.collection(FirebaseConst.USER_COLLECTION)
                .whereEqualTo("uid", auth.currentUser!!.uid)
                .addSnapshotListener{ snapshot, error ->
                    if (error != null){
                        Toast.makeText(activity, error.message, Toast.LENGTH_LONG).show()
                        return@addSnapshotListener
                    }
                    snapshot?.documents?.forEach{
                        val user = it.toObject(User::class.java)
                        view.profile_email.text = user?.username
                        view.profile_phone.text = user?.phone
                    }
                }
        }
        return view
    }


}
