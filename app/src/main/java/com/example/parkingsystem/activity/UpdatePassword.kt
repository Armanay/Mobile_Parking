package com.example.parkingsystem.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.parkingsystem.FirebaseConst
import com.example.parkingsystem.R
import com.example.parkingsystem.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_update_password.*

class UpdatePassword : AppCompatActivity() {

    private val db by lazy{ FirebaseFirestore.getInstance()}
    private val auth by lazy{ FirebaseAuth.getInstance()}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_password)

        toolbarSettings()
        getOldPassword(auth.currentUser!!.uid, old_pswd.text.toString())
    }

    private fun toolbarSettings(){
        supportActionBar!!.title = "Изменение пароля"
        supportActionBar!!.setDisplayShowHomeEnabled(true);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    private fun getOldPassword(uid:String, oldP: String){
        db.collection(FirebaseConst.USER_COLLECTION)
            .document(uid)
            .addSnapshotListener{snapshot, e->
                if (e != null){
                    Log.d("taaag", e.localizedMessage)
                    return@addSnapshotListener
                }

                val user = snapshot?.toObject(User::class.java)!!
                if (user.password == oldP){
                   updatePassword(uid)
                }
                else{
                    old_pswd.error = "Incorrect password"
                }

            }

    }

    private fun updatePassword(uid: String){
        if (new_pswd.text.toString() == new_pswd_confirm.text.toString()) {
            db.collection(FirebaseConst.USER_COLLECTION)
                .document(uid)
                .update("password",new_pswd.text.toString())
                .addOnSuccessListener {
                    Toast.makeText(this, "Updated", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
                }
        }
        else{
            new_pswd_confirm.error = "Passwords mismatch"
        }
    }
}
