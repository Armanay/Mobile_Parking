package com.example.parkingsystem.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingsystem.FirebaseConst
import com.example.parkingsystem.R
import com.example.parkingsystem.adapter.LeftPlaceAdapter
import com.example.parkingsystem.binder.ParkingDetailBinder
import com.example.parkingsystem.entity.ParkingSpace
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.custom_bottom_fragment_toolbar.view.*
import kotlinx.android.synthetic.main.fragment_place_list.view.*


class PlaceListFragment : BottomSheetDialogFragment() {

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }
    lateinit var com: ParkingDetailBinder
    private var parkingName: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_place_list, container, false)
        val mArgs = arguments
        parkingName = mArgs!!.getString(ParkingFragment.PARKING)
        initSpaceList(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.refresh_places.setOnClickListener {
            initSpaceList(view)
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initSpaceList(view: View){
        view.space_list_view.layoutManager = LinearLayoutManager(activity?.baseContext)
        if (parkingName != null){
            db.collection(FirebaseConst.PARKING_SPACE_COLLECTION)
                .whereEqualTo("parkingName", parkingName)
                .whereEqualTo("free", true)
                .addSnapshotListener{ it, e ->
                    val spaces = it?.documents?.map {
                        it.toObject(ParkingSpace::class.java)
                    } ?: emptyList()


                    if(!spaces.isNullOrEmpty()){
                        view.space_list_view.adapter = LeftPlaceAdapter(
                            spaces as List<ParkingSpace>,
                            onClick = {
                                com.chosenPlace(it)
                                dismiss()
                            })
                    }
                    else Toast.makeText(activity?.baseContext, "Место пусто", Toast.LENGTH_SHORT).show()
                }
        }
        designRecyclerView(view.space_list_view)
    }
    private fun designRecyclerView(view: RecyclerView){
        (view.layoutManager as LinearLayoutManager).reverseLayout = true
        val mDividerItemDecoration = DividerItemDecoration(
            view.context,
            (view.layoutManager as LinearLayoutManager).orientation
        )
        view.addItemDecoration(mDividerItemDecoration)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        com = targetFragment as ParkingDetailBinder
    }

}