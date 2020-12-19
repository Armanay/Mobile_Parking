package com.example.parkingsystem.activity


import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.parkingsystem.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_verification.*
import kotlinx.android.synthetic.main.verify_popup_window.*
import kotlinx.android.synthetic.main.verify_popup_window.view.*
import java.util.concurrent.TimeUnit

class Verification : AppCompatActivity() {

    companion object{
        const val VERIF = "verif"
        const val CODE = "code"
    }

    private val auth by lazy{ FirebaseAuth.getInstance()}
    lateinit var bView: Dialog
    lateinit var verifId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)
        get_sms_btn.setOnClickListener {
            verificationCallback(phone_num_user.text.toString())
            val alertDialog = Dialog(this)

            alertDialog.setContentView(R.layout.verify_popup_window)
            val verifyBtn =
                alertDialog.findViewById<Button>(R.id.ver_btn)

            val verCodeText  = alertDialog.findViewById<EditText>(R.id.ver_code)
            alertDialog.show()
            verifyBtn.setOnClickListener {
                if (verCodeText.text!!.isNotEmpty()){
                    val code = verCodeText.text.toString()
                    val credential = PhoneAuthProvider.getCredential(verifId, code)
                    signIn(credential)
                    alertDialog.dismiss()
                }
            }
            bView = alertDialog

        }
        toolbarSettings()
    }
    private fun toolbarSettings(){
        supportActionBar!!.title = "Подтверждение номера"
        supportActionBar!!.setDisplayShowHomeEnabled(true);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    private fun verificationCallback(phoneNumber: String) {

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signIn(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException) {
                    toast(e.localizedMessage)
                    Log.d("taaaag", "credentiallllllllll")
                } else if (e is FirebaseTooManyRequestsException) {
                    toast(e.localizedMessage)
                    Log.d("taaaags", "requestssssssss")

                }
            }

            override fun onCodeSent(
                verification: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                verifId = verification
            }
        })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    private fun signIn(credential: PhoneAuthCredential){
            auth.signInWithCredential(credential)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(this,"Success",Toast.LENGTH_LONG).show()
                        successVerification()
                   }
                    else{
                        Log.d("errrrrrrrorrr", it.toString())
                   }
        }

    }
    private fun successVerification(){
        val intent = Intent(this, RegistrationActivity::class.java)
        intent.putExtra(RegistrationActivity.PHONE, phone_num_user.text.toString())
        startActivity(intent)
        finish()
    }

    private fun toast(str: String){
        Toast.makeText(this,str,Toast.LENGTH_LONG).show()
    }
}
