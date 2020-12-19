package com.example.parkingsystem.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.widget.Toast
import com.example.parkingsystem.FirebaseConst
import com.example.parkingsystem.R
import com.example.parkingsystem.entity.Car
import com.example.parkingsystem.entity.ParkingSpace
import com.example.parkingsystem.entity.User
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.getInstance
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_registration.*
import java.util.concurrent.TimeUnit

class RegistrationActivity : AppCompatActivity() {

    companion object{
        const val PHONE = "phone"
    }

    private val auth by lazy{ FirebaseAuth.getInstance()}
    private val db by lazy{ FirebaseFirestore.getInstance()}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth.signOut()
        setContentView(R.layout.activity_registration)
        (this as AppCompatActivity).supportActionBar?.title = "Регистрация"
        val phoneNo = intent.getStringExtra(PHONE)
        phoneNo?.let { signUp(it) }
    }

    private fun signUp(phoneNo: String){
        sign_up_btn.setOnClickListener {
            if (reg_username.text!!.isEmpty() || reg_password.text!!.isEmpty() || reg_password_again.text!!.isEmpty()) {
                if (reg_username.text!!.isEmpty()) {
                    reg_username.error = FirebaseConst.ERROR_MSG_IS_EMPTY
                }
                if (reg_password.text!!.isEmpty()) {
                    reg_password.error = FirebaseConst.ERROR_MSG_IS_EMPTY
                }
                if (reg_password_again.text!!.isEmpty()) {
                    reg_password_again.error = FirebaseConst.ERROR_MSG_IS_EMPTY
                }
            } else {
                if (reg_password.text.toString() == reg_password_again.text.toString()) {
                    auth.createUserWithEmailAndPassword("${phoneNo}@mail.ru", reg_password.text.toString())
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                Log.d("tagtagtag", "signInWithEmail:success")

                                val parkingSpace = ParkingSpace(
                                    "Не выбран",
                                    true,
                                    "Не выбран",
                                    0,
                                    "Не выбран",
                                    "Не выбран"
                                )
                                val car = Car("Не выбран", "Не выбран", "Не выбран", "Не выбран")

                                val user = User(
                                    auth.currentUser!!.uid,
                                    reg_username.text.toString(),
                                    phoneNo,
                                    reg_password.text.toString(),
                                    ArrayList(),
                                    parkingSpace,
                                    Car()
                                )
                                successReg()
                                saveUserData(user.uid, user)
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("tagtagtag", "signInWithEmail:failure", task.exception)
                                Toast.makeText(
                                    baseContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            else {
                    Toast.makeText(this, "Passwords do not match!!!", Toast.LENGTH_LONG).show()
                }
            }
        }

    }
    private fun successReg(){
        val intent = Intent(this, ParkingActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun saveUserData(
        id: String,
        user: User
    ){
        db.collection(FirebaseConst.USER_COLLECTION)
            .document(id)
            .set(user).addOnSuccessListener {
                Log.d("tagtag",FirebaseConst.SUCCESS_MSG)
            }
    }








}
