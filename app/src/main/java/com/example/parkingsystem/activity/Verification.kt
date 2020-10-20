package com.example.parkingsystem.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.parkingsystem.R
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_verification.*
import java.util.concurrent.TimeUnit

class Verification : AppCompatActivity() {

    companion object{
        const val VERIF = "verif"
        const val CODE = "code"
    }

    lateinit var mCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private val auth by lazy{ FirebaseAuth.getInstance()}

    var verificationId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)
        (this as AppCompatActivity).supportActionBar?.title = "Подтверждение номера"
        get_sms_btn.setOnClickListener {
            verifyPhone(phone_num_user.text.toString())
            Toast.makeText(this,"Wait please!", Toast.LENGTH_LONG).show()
        }
    }
    private fun verificationCallback() {
        mCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                authenticate()
            }

            override fun onVerificationFailed(e: FirebaseException) {


                if (e is FirebaseAuthInvalidCredentialsException) {
                    toast(e.localizedMessage)
                } else if (e is FirebaseTooManyRequestsException) {
                    toast(e.localizedMessage)
                }
            }

            override fun onCodeSent(
                verification: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {

                verificationId = verification.toString()
            }
        }
    }
    private fun signIn(credential: PhoneAuthCredential){

            auth.signInWithCredential(credential)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(this,"Success",Toast.LENGTH_LONG).show()
                        successVerification()
                   }
        }

    }
    private fun authenticate(){

        ver_btn.setOnClickListener {
            val code = ver_code.text.toString()
            val credential = PhoneAuthProvider.getCredential(verificationId,code)
            signIn(credential)
        }

    }
    private fun successVerification(){
        val intent = Intent(this, RegistrationActivity::class.java)
        intent.putExtra(RegistrationActivity.PHONE, phone_num_user.text.toString())
        startActivity(intent)
        finish()
    }
    private fun verifyPhone(phoneNumber: String){
        verificationCallback()
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, mCallback)
    }
    private fun toast(str: String){
        Toast.makeText(this,str,Toast.LENGTH_LONG).show()
    }
}
