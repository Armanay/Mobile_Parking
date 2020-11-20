package com.example.parkingsystem.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.parkingsystem.R

class BankCardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_card)
        toolbarSettings()
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
}
