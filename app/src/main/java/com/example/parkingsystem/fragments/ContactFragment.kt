package com.example.parkingsystem.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telecom.Call
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.parkingsystem.FirebaseConst

import com.example.parkingsystem.R
import com.example.parkingsystem.entity.Error
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_contact.*
import kotlinx.android.synthetic.main.fragment_contact.view.*

/**
 * A simple [Fragment] subclass.
 */
class ContactFragment : Fragment() {
    companion object{
        const val REQUEST_CODE = 1
    }
    private val auth by lazy{ FirebaseAuth.getInstance()}
    private val db by lazy{ FirebaseFirestore.getInstance()}
    val str = ArrayList<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_contact, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = "Свяжитесь с нами"
            view.phone_call.setOnClickListener {
                makePhoneCall()
            }
        view.send_error.setOnClickListener {
            sendError(auth.currentUser!!.uid, support_msg.text.toString())
        }
        return view
    }

    private fun makePhoneCall(){
        val phone = phone_call_text.text.toString()
        if (ContextCompat.checkSelfPermission(
                this.requireActivity(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            val permissions = arrayOf(Manifest.permission.CALL_PHONE)
            ActivityCompat.requestPermissions(requireActivity(),
                permissions, REQUEST_CODE)

        }
        else{
            val n = "tel:" + phone
            startActivity(Intent(Intent.ACTION_CALL, Uri.parse(n)))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == REQUEST_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                makePhoneCall()
            }else{
                Log.d("tagtag", "rerereo")
            }
        }
    }

    private fun sendError(uid:String, msg: String){
        val newError = db.collection(FirebaseConst.ERROR_COLLECTION).document()
        val error = Error(
                newError.id, msg, uid
        )
        db.collection(FirebaseConst.ERROR_COLLECTION)
            .document(error.id)
            .set(error)
            .addOnSuccessListener {
                Toast.makeText(activity,"Error was saved", Toast.LENGTH_LONG).show()
                support_msg.text?.clear()
            }

    }

}
