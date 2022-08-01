package com.kspt.db.schedule2021

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val exitBtn = findViewById<Button>(R.id.exitButton)
        exitBtn.setOnClickListener {
            val exitIntent = Intent(this, MainActivity::class.java)
            exitIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            getSharedPreferences("auth", Context.MODE_PRIVATE).edit().putBoolean("isLoggedIn", false).apply()
            startActivity(exitIntent)
        }

        val adminCheck = isUserIsAdmin()
        val adminBtn = findViewById<Button>(R.id.admin_btn)
        if (adminCheck) adminBtn.visibility = View.VISIBLE
        adminBtn.setOnClickListener {
            val adminIntent = Intent(this, AdminRegActivity::class.java)
            startActivity(adminIntent)
        }


    }

    private fun isUserIsAdmin() : Boolean {
        val claims = extractAllClaims(getSharedPreferences("jwt", Context.MODE_PRIVATE)
            .getString("jwt", " ")!!)
        val role = claims.get("role", String::class.java)
        return role == "ADMIN"
    }
}