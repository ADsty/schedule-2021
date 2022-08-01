package com.kspt.db.schedule2021

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import kotlinx.android.synthetic.main.event.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class NewDeadlineActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_deadline)

        val subjects = arrayOf("АПДУ", "Телеком", "БД", "АЭВМ") //поменять на запрос

        val spinner = findViewById<Spinner>(R.id.subject_spinner)
        val titleET = findViewById<EditText>(R.id.title_edittext)
        val dateET = findViewById<EditText>(R.id.date_edittext)
        val timeET = findViewById<EditText>(R.id.time_edittext)
        val descriptionET = findViewById<EditText>(R.id.description_edittext)


        val saveButton = findViewById<Button>(R.id.save_btn)
        saveButton.setOnClickListener {

            val subject = spinner.selectedItem.toString()
            val title = titleET.text.toString()
            val date = dateET.text.toString()
            val time = timeET.text.toString()
            val description = descriptionET.text.toString()
            val token = getSharedPreferences("jwt", Context.MODE_PRIVATE).getString("jwt", " ")!!
            val claims = extractAllClaims(token)
            val id = claims.get("id", Integer::class.java)
            postNewDeadline(subject, title, date, time, description, id)

            val intent = Intent(this, DeadlineActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        JsonTask(this).execute()
    }

    private inner class JsonTask internal constructor(context: Context): AsyncTask<String, String, String>() {

        private val mContext = context

        override fun doInBackground(vararg params: String): String? {
            val token = getSharedPreferences("jwt", Context.MODE_PRIVATE).getString("jwt", " ")!!
            val response = getApiResponse("http://10.0.2.2:8832/students/1/subjects", true, token)
            return response
        }

        override fun onPostExecute(response: String) {
            super.onPostExecute(response)
            val subjectContainer: Array<Subject> = gson.fromJson(response, Array<Subject>::class.java)
            val subjects = mutableListOf<String>()
            subjectContainer.forEach { subjects.add(it.name) }
            val spinner: Spinner = findViewById(R.id.subject_spinner)
            spinner.adapter = ArrayAdapter(mContext, android.R.layout.simple_spinner_dropdown_item, subjects)
        }
    }

    private fun postNewDeadline(subject: String, title: String, date: String, time: String, description: String, studentId: Integer): Boolean{
        var response: String? = null
        var completed = false
        val job = lifecycle.coroutineScope.launch {
            async(Dispatchers.IO) {
                response = postDeadline(subject, title, date, time, description, studentId)
                completed = true
            }.await()
        }
        while (!completed);
        return response != null
    }

}