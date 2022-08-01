package com.kspt.db.schedule2021

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import android.os.AsyncTask

class DeadlineActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            if (item.itemId == R.id.navigation_schedule) {
                val intent = Intent(this, ScheduleActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deadline)
        val navigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val button = findViewById<Button>(R.id.add_button)

        button.setOnClickListener {
            val intent = Intent(this, NewDeadlineActivity::class.java)
            startActivity(intent)
        }

        val settingsButton = findViewById<Button>(R.id.settings_button)

        settingsButton.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        JsonTask(this).execute()
    }

    private fun isInEventList(date: String, data: List<Deadline>): Int {
        println(date)
        var n = 0
        data.forEach {
            if (it.deadlineDate == (date)) return n
            else n++
        }
        return -1
    }

    private fun convertToString(date: CalendarDay): String =
        String.format("%02d-%02d-%02d", date.year, date.month, date.day)

    private inner class JsonTask internal constructor(context: Context) : AsyncTask<String, String, String>() {

        private val mContext: Context = context

        override fun doInBackground(vararg params: String): String? {
            val token = getSharedPreferences("jwt", Context.MODE_PRIVATE).getString("jwt", " ")!!
            val claims = extractAllClaims(token)
            val id = claims.get("id", Integer::class.java)
            val response = getApiResponse("http://10.0.2.2:8832/students/$id/deadlines", true, token)
            return response
        }

        override fun onPostExecute(response: String) {
            super.onPostExecute(response)
            val token = getSharedPreferences("jwt", Context.MODE_PRIVATE).getString("jwt", " ")!!
            val deadlines = mutableListOf<CalendarDay>()
            val deadlineContainer: Array<Deadline> = gson.fromJson(response, Array<Deadline>::class.java)
            deadlineContainer.sortBy { it.deadlineDate }
            deadlineContainer.forEach { deadlines.add(it.dateToCalendarDay()) }
            val recyclerView = findViewById<RecyclerView>(R.id.list)
            recyclerView.layoutManager = LinearLayoutManager(mContext)
            recyclerView.adapter = RecyclerViewAdapter(deadlineContainer.toList(), mContext, token)

            val calendarView = findViewById<MaterialCalendarView>(R.id.calendarView)
            calendarView.tileHeight = windowManager.defaultDisplay.height / 16
            calendarView.setOnDateChangedListener { _, date, _ ->
                val count = isInEventList(convertToString(date), deadlineContainer.toList())
                if (count >= 0) {
                    recyclerView.scrollToPosition(count)
                }
            }
        }
    }
}