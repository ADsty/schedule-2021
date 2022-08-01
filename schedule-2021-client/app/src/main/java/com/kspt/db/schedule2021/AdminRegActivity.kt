package com.kspt.db.schedule2021

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AdminRegActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_registration)

        val regBtn = findViewById<Button>(R.id.admin_rg_conf_button)

        regBtn.setOnClickListener {
            val login = findViewById<EditText>(R.id.editLogin)
            val groupName = findViewById<EditText>(R.id.editGroupName)
            val password = findViewById<EditText>(R.id.editPassword)
            val role = findViewById<EditText>(R.id.editRole)

            if (login.text.toString() == "" || groupName.text.toString() == ""
                || password.text.toString() == "" || role.text.toString() == ""
            ) {
                Toast.makeText(
                    this,
                    "Заполните все поля для регистрации",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val job = lifecycle.coroutineScope.launch {
                    val result = async(Dispatchers.IO) {
                        val response = registerUser(
                            login.text.toString(),
                            password.text.toString(),
                            groupName.text.toString(),
                            role.text.toString()
                        )
                        println(response.toString())

                    }.await()
                }
                val regIntent = Intent(this, NewDeadlineActivity::class.java)
                startActivity(regIntent)
            }
        }
    }
}
