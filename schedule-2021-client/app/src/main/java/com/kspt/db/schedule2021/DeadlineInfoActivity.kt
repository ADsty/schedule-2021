package com.kspt.db.schedule2021

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DeadlineInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deadline_information)

        val arguments = intent.extras

        val subjectNameExtra = arguments?.get("subject").toString()
        val dateExtra = arguments?.get("date").toString()
        val timeExtra = arguments?.get("time").toString()
        val titleExtra = arguments?.get("title").toString()
        val descriptionExtra = arguments?.get("description").toString()

        val subjectNameView = findViewById<TextView>(R.id.subjectName)
        val dateView = findViewById<TextView>(R.id.deadlineDate)
        val timeView = findViewById<TextView>(R.id.notificationTime)
        val titleView = findViewById<TextView>(R.id.deadlineTitle)
        val descriptionView = findViewById<TextView>(R.id.deadlineDescription)

        subjectNameView.text = subjectNameExtra
        dateView.text = dateExtra
        timeView.text = timeExtra
        titleView.text = titleExtra
        descriptionView.text = descriptionExtra
    }
}