package com.example.parkingsystem.activity

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingsystem.FirebaseConst
import com.example.parkingsystem.R
import com.example.parkingsystem.adapter.UserCardAdapter
import com.example.parkingsystem.entity.Card
import com.example.parkingsystem.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_bank_card.*

class BankCardActivity : AppCompatActivity() {

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_card)
        toolbarSettings()
        initCardList(auth.currentUser!!.uid)
        add_user_card.setOnClickListener {
            addCardDialog()
        }
    }

    private fun toolbarSettings(){
        supportActionBar!!.title = "Банковские карты"
        supportActionBar!!.setDisplayShowHomeEnabled(true);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun addCardDialog(){
        val alertDialog = Dialog(this)

        alertDialog.setContentView(R.layout.add_card_dialog)
        val btn = alertDialog.findViewById<TextView>(R.id.pay_btn)
        val cardNum = alertDialog.findViewById<TextView>(R.id.user_payment_card)
        val cardOwner = alertDialog.findViewById<TextView>(R.id.payment_card_owner_name)
        val cvc = alertDialog.findViewById<TextView>(R.id.user_card_cvc2_cvv2)
        val expDate = alertDialog.findViewById<TextView>(R.id.user_card_expiration_date)
        alertDialog.show()
        btn.setOnClickListener {
            if (cardNum.text.toString().isNotEmpty() && cardOwner.text.toString().isNotEmpty() && cvc.text.toString().isNotEmpty() && expDate.text.toString().isNotEmpty()){
                db.collection(FirebaseConst.USER_COLLECTION)
                    .document(auth.currentUser!!.uid)
                    .update("cardList",
                        FieldValue.arrayUnion(Card(
                            cardNum = cardNum.text.toString(), cardOwner = cardOwner.text.toString(), cvc = cvc.text.toString(), expDate = expDate.text.toString()
                        ))
                        )
                    .addOnSuccessListener {
                        alertDialog.dismiss()

                        Toast.makeText(this, "Added", Toast.LENGTH_LONG).show()

                    }.addOnFailureListener {
                        Toast.makeText(this, "Something is went wrong. Try later!", Toast.LENGTH_LONG).show()

                    }
            }
            else{
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_LONG).show()

            }
        }

    }

    private fun initCardList(uid:String){
        user_card_list.layoutManager = LinearLayoutManager(this)
        db.collection(FirebaseConst.USER_COLLECTION)
            .document(uid)
            .addSnapshotListener { snapshot, e ->
                if (e != null){
                    Log.d("tagtagtag", e.message.toString())
                    return@addSnapshotListener
                }
                val user = snapshot?.toObject(User::class.java)
                if(user != null){
                    if (user.cardList.isEmpty()) {
                        empty_card_list.isVisible = true
                        my_cards_label.isVisible = false
                    }else{
                        empty_card_list.isVisible = false
                        my_cards_label.isVisible = true
                    }


                    user_card_list.adapter = UserCardAdapter(user.cardList)

                }
            }

        designRecyclerView()
    }

    private fun designRecyclerView(){
        (user_card_list.layoutManager as LinearLayoutManager).reverseLayout = true
        (user_card_list.layoutManager as LinearLayoutManager).stackFromEnd = true
        val mDividerItemDecoration = DividerItemDecoration(
            user_card_list.context,
            (user_card_list.layoutManager as LinearLayoutManager).getOrientation()
        )
        user_card_list.addItemDecoration(mDividerItemDecoration)
    }

}
