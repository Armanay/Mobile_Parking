package com.example.parkingsystem.activity

import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.sapereaude.maskedEditText.MaskedEditText
import com.example.parkingsystem.FirebaseConst
import com.example.parkingsystem.R
import com.example.parkingsystem.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val auth by lazy{ FirebaseAuth.getInstance()}
    private val db by lazy{ FirebaseFirestore.getInstance()}
    lateinit var  mEditMaskedCustom: MaskedEditText
    private var userEmail: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (this as AppCompatActivity).supportActionBar?.title = "Логин"
        signIn()
        registration()
    }

    private fun signIn(){
        sign_in_btn.setOnClickListener {
            if (login_email.text!!.isEmpty() || login_psw.text!!.isEmpty()) {
                if (login_email.text!!.isEmpty()) {
                    login_email.error = FirebaseConst.ERROR_MSG_IS_EMPTY
                }
                if (login_psw.text!!.isEmpty()) {
                    login_psw.error = FirebaseConst.ERROR_MSG_IS_EMPTY
                }
            } else {



                db.collection(FirebaseConst.USER_COLLECTION)
                    .whereEqualTo("phone", login_email.text.toString())
                    .addSnapshotListener {snapshot, e->
                     if (e != null){
                         Log.d("tagtag", e.localizedMessage)
                         return@addSnapshotListener
                     }
                        val users = snapshot?.documents?.map {
                            it.toObject(User::class.java)!!
                        }?: emptyList()

                        for (u in users){
                            if (u.password == login_psw.text.toString()){
                                userEmail = "${login_email.text.toString()}@mail.ru"
                                auth.signInWithEmailAndPassword(userEmail, login_psw.text.toString())
                                    .addOnCompleteListener(this) { task ->
                                        if (task.isSuccessful) {
                                            successLogin()
                                            Log.d("tagtagtag", "signInWithEmail:success")
                                            successLogin()
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w("tagtagtag", "signInWithEmail:failure", task.exception)
                                            Toast.makeText(baseContext, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            }
                            else{
                                Toast.makeText(this,"Password is not correct!",Toast.LENGTH_LONG).show()
                            }
                        }

                    }
            }
        }
    }
    private fun successLogin(){
        val intent = Intent(this, ParkingActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun registration(){
        sign_up_link.setOnClickListener {
            val intent = Intent(this, Verification::class.java)
            startActivity(intent)
        }
    }
}

