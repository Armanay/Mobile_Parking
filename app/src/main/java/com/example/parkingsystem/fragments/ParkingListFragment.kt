package com.example.parkingsystem.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingsystem.FirebaseConst
import com.example.parkingsystem.R
import com.example.parkingsystem.adapter.ParkingAdapter
import com.example.parkingsystem.entity.Parking
import com.example.parkingsystem.binder.ParkingDetailBinder
import com.example.simplechatapp.communcators.ParkingSpaceDetailBinder
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_parking_list.view.*

class ParkingListFragment : BottomSheetDialogFragment() {
    lateinit var com: ParkingDetailBinder
    private val db by lazy { FirebaseFirestore.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_parking_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initCarList(view)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initCarList(view: View){
        view.parking_list_view.layoutManager = LinearLayoutManager(activity?.baseContext)
        db.collection(FirebaseConst.PARKING_COLLECTION)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                val parkings = querySnapshot?.documents?.map {
                    it.toObject(Parking::class.java)
                } ?: emptyList()
                if (!parkings.isNullOrEmpty())
                {
                    view.parking_list_view.adapter = ParkingAdapter(parkingList = parkings as List<Parking>,
                    onClick = {
                        com.chosenParking(parking = it)
                        dismiss()
                    })
                }
            }
        designRecyclerView(view)
    }

    private fun designRecyclerView(view: View){
        (view.parking_list_view.layoutManager as LinearLayoutManager).reverseLayout = true
        (view.parking_list_view.layoutManager as LinearLayoutManager).stackFromEnd = true
        val mDividerItemDecoration = DividerItemDecoration(
            view.parking_list_view.context,
            (view.parking_list_view.layoutManager as LinearLayoutManager).getOrientation()
        )
        view.parking_list_view.addItemDecoration(mDividerItemDecoration)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        com = targetFragment as ParkingDetailBinder
    }
}