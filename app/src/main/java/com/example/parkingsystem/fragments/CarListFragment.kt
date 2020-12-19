package com.example.parkingsystem.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingsystem.FirebaseConst
import com.example.parkingsystem.R
import com.example.parkingsystem.adapter.CarAdapter
import com.example.parkingsystem.entity.User
import com.example.parkingsystem.binder.ParkingDetailBinder
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_car_list.view.*

class CarListFragment : BottomSheetDialogFragment() {

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }
    lateinit var com: ParkingDetailBinder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_car_list, container, false)
        initCarList(view, auth.currentUser!!.uid)
        return view
    }



    private fun initCarList(view: View, uid:String){
        view.car_list_view.layoutManager = LinearLayoutManager(activity?.baseContext)
        db.collection(FirebaseConst.USER_COLLECTION)
            .document(uid)
            .addSnapshotListener { snapshot, e ->
                if (e != null){
                    Log.d("tagtagtag", e.message.toString())
                    return@addSnapshotListener
                }
                val user = snapshot?.toObject(User::class.java)
                if(user != null){
                    view.car_list_view.adapter = CarAdapter(user.carList,
                    onClick = {
                        com.chosenCar(it)
                        dismiss()
                    },identifier =  1)
                }
            }

        designRecyclerView(view.car_list_view)
    }

    private fun designRecyclerView(view: RecyclerView){
        (view.layoutManager as LinearLayoutManager).reverseLayout = true
        (view.layoutManager as LinearLayoutManager).stackFromEnd = true
        val mDividerItemDecoration = DividerItemDecoration(
            view.context,
            (view.layoutManager as LinearLayoutManager).getOrientation()
        )
        view.addItemDecoration(mDividerItemDecoration)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        com = targetFragment as ParkingDetailBinder
    }
}