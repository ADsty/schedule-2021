package com.kspt.db.schedule2021

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("auth", Context.MODE_PRIVATE)
    }

    override fun onResume() {
        super.onResume()
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if(isLoggedIn){
            val scheduleIntent = Intent(this, ScheduleActivity::class.java)
            startActivityForResult(scheduleIntent, 0)
        }
        else{
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivityForResult(loginIntent, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == 1)
            finish()
    }
}