package com.example.parkingsystem.activity


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)
        get_sms_btn.setOnClickListener {
            verificationCallback(phone_num_user.text.toString())
            val dialogBottom = BottomSheetDialog(applicationContext)
            val bottomSheetView = layoutInflater.inflate(
                R.layout.verify_popup_window, null
            )
            dialogBottom.setContentView(bottomSheetView)
            dialogBottom.show()
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
//                val bottomSheetDialog =
//                    BottomSheetDialog(this)
//                val bottomSheetView = LayoutInflater.from(applicationContext)
//                    .inflate(
//                        R.layout.verify_popup_window,
//                        findViewById(R.id.bottom_sheet_window)
//                    )
//                val verifyBtn =
//                    bottomSheetView.findViewById<Button>(R.id.ver_btn)
                ver_btn.setOnClickListener {

                    if (ver_code.text!!.isNotEmpty()){
                        val code = ver_code.text.toString()
                        val credential = PhoneAuthProvider.getCredential(verification, code)
                        signIn(credential)
                    }
                }
//                bottomSheetDialog.setContentView(bottomSheetView)
//                bottomSheetDialog.show()


//                val bottomSheetView = layoutInflater.inflate(
//                    R.layout.verify_popup_window, null
//                )
//                val bottomSheetDialog =
//                    BottomSheetDialog(applicationContext)applicationContext
//                bottomSheetView.ver_btn.setOnClickListener {
//
//               }


            }
        })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    private fun signIn(credential: PhoneAuthCredential){
            ver_code.setText(credential.smsCode.toString())
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
