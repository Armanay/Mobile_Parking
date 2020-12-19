package com.example.parkingsystem.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingsystem.FirebaseConst
import com.example.parkingsystem.R
import com.example.parkingsystem.entity.Card
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.layout_user_card_item.view.*

class UserCardAdapter(
    private val cardList: List<Card> = listOf()
):RecyclerView.Adapter<UserCardAdapter.CarViewHolder>() {
    private val db by lazy{ FirebaseFirestore.getInstance()}
    private val auth by lazy{ FirebaseAuth.getInstance()}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_user_card_item, parent, false)
        return CarViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        holder.bindCar(cardList[position])
    }


    inner class CarViewHolder(
        private val view: View
    ):RecyclerView.ViewHolder(view){
        fun bindCar(card: Card){
        view.card_item_number.text = card.cardNum


            view.delete_user_card.setOnClickListener {
                db.collection(FirebaseConst.USER_COLLECTION)
                    .document(auth.currentUser!!.uid)
                    .update("cardList", FieldValue.arrayRemove(card))
                    .addOnSuccessListener {
                        Log.d("taaag", "deleted")
                    }
                    .addOnFailureListener{
                        Log.d("taaag", it.localizedMessage)
                    }

            }
        }
    }
}