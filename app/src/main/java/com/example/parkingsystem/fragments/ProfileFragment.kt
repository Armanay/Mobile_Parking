package com.example.parkingsystem.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.parkingsystem.FirebaseConst
import com.example.parkingsystem.R
import com.example.parkingsystem.activity.BankCardActivity
import com.example.parkingsystem.activity.UpdatePassword
import com.example.parkingsystem.activity.UserCarList
import com.example.parkingsystem.entity.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.header.*
import java.io.IOException
import java.util.*

class ProfileFragment : Fragment() {

    private val db by lazy{ FirebaseFirestore.getInstance()}
    private val auth by lazy{ FirebaseAuth.getInstance()}
    private val storage by lazy{ FirebaseStorage.getInstance()}

    companion object{
        const val PICK_IMAGE_REQUEST = 22
    }
    private var filePath: Uri? = null
    private var storageReference: StorageReference? = storage.reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_profile, container, false)
        profileInfo(view)
        profileSections(view)
        (activity as AppCompatActivity).supportActionBar?.title = "Профиль"
        return profileInfo(view)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.profile_img.setOnClickListener {
            uploadImage()
        }
    }

    private fun profileSections(view: View){
        view.my_cars.setOnClickListener {
            val intent = Intent(activity, UserCarList::class.java)
            startActivity(intent)
        }
        view.bank_cards.setOnClickListener {
            val intent = Intent(activity, BankCardActivity::class.java)
            startActivity(intent)
        }
        view.update_pswd.setOnClickListener {
            val intent = Intent(activity, UpdatePassword::class.java)
            startActivity(intent)
        }
    }

    private fun profileInfo(view: View): View{

        if (auth.currentUser != null){
            db.collection(FirebaseConst.USER_COLLECTION)
                .whereEqualTo("uid", auth.currentUser!!.uid)
                .addSnapshotListener{ snapshot, error ->
                    if (error != null){
                        Toast.makeText(activity, error.message, Toast.LENGTH_LONG).show()
                        return@addSnapshotListener
                    }
                    snapshot?.documents?.forEach{
                        val user = it.toObject(User::class.java)
                        view.profile_email.text = user?.username
                        view.profile_phone.text = user?.phone
                        if (user?.imgUri != null) Picasso.get().load(user.imgUri).noFade().into(view.profile_img)
                    }
                }
        }
        return view
    }


    private fun uploadImage(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
            PICK_IMAGE_REQUEST
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
            && data != null && data.data != null){
            filePath = data.data
            try {
                val bitmap =
                    MediaStore.Images.Media.getBitmap(activity?.contentResolver, filePath)
                Log.d("oooooooooo", bitmap.toString())
                profile_img.setImageBitmap(bitmap)
                uploadImgStorage()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImgStorage() = if (filePath != null){
        val ref = storageReference?.child("profileImg/" + UUID.randomUUID().toString())
        val uploadTask = ref?.putFile(filePath!!)

        val urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation ref.downloadUrl
        })?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                addUploadRecordToDb(downloadUri.toString())
            } else {
                Log.d("uploadImgStorage", task.toString())
            }
        }?.addOnFailureListener{
            Log.d("imgStorageException", it.toString())

        }
    }else{
        Toast.makeText(activity?.baseContext,"Please Upload an Image", Toast.LENGTH_SHORT).show()
    }

    private fun addUploadRecordToDb(uri: String){
        db.collection(FirebaseConst.USER_COLLECTION)
            .document(auth.currentUser!!.uid)
            .update("imgUri", uri)
            .addOnSuccessListener {
                Toast.makeText(activity?.baseContext, "Saved to DB", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(activity?.baseContext, "Error saving to DB", Toast.LENGTH_LONG).show()
            }
    }


}
