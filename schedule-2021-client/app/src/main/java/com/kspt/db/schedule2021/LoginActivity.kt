package com.kspt.db.schedule2021

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var sharedPreferences : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("jwt", Context.MODE_PRIVATE)
        setContentView(R.layout.activity_login)

        val loginButton = findViewById<Button>(R.id.login_btn)

        val usernameText = findViewById<EditText>(R.id.editUsername)
        val passwordText = findViewById<EditText>(R.id.editPassword)

        loginButton.setOnClickListener {
            val check = checkAuthorization(usernameText.text.toString(), passwordText.text.toString())
            if (check) {
                val scheduleIntent = Intent(this, ScheduleActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                getSharedPreferences("auth", Context.MODE_PRIVATE).edit().putBoolean("isLoggedIn", true).apply()
                startActivity(scheduleIntent)
            } else {
                Toast.makeText(
                    this,
                    "Неверный логин и/или пароль, попробуйте снова",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun checkAuthorization(username: String, password: String): Boolean {
        var response: String? = null
        var completed = false
        val job = lifecycle.coroutineScope.launch {
            val result = async(Dispatchers.IO) {
                response = loginUser(username, password)
                println(response)
                val jwtContainer = gson.fromJson(response, JWTContainer::class.java)
                sharedPreferences.edit().putString("jwt", jwtContainer.jwt).apply()
                completed = true
            }.await()
        }
        while (!completed);
        return response != null
    }

    override fun onBackPressed() {
        setResult(1)
        finish()
    }
}