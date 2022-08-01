package com.kspt.db.schedule2021

import java.util.*
import java.time.*
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView

@RequiresApi(Build.VERSION_CODES.O)
class ScheduleActivity : AppCompatActivity() {

    private var date: LocalDate = LocalDate.now()

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            if (item.itemId == R.id.navigation_deadlines) {
                val intent = Intent(this, DeadlineActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        val navigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val previousWeekButton = findViewById<ImageButton>(R.id.previous_week)
        val nextWeekButton = findViewById<ImageButton>(R.id.next_week)

        previousWeekButton.setOnClickListener{
            date = date.minusDays(7)
            JsonTask(this).execute()
        }
        nextWeekButton.setOnClickListener{
            date = date.plusDays(7)
            JsonTask(this).execute()
        }
    }

    override fun onBackPressed() {
        setResult(1)
        finish()
    }

    override fun onResume() {
        super.onResume()
        JsonTask(this).execute()
    }

    private inner class JsonTask internal constructor(context: Context): AsyncTask<String, String, String>() {

        private val mContext: Context = context

        override fun onPreExecute(){
            super.onPreExecute()
            val noLessonTV = findViewById<TextView>(R.id.no_lessons_tv)
            noLessonTV.visibility = View.GONE
        }

        override fun doInBackground(vararg params: String): String? {
            val token = getSharedPreferences("jwt", Context.MODE_PRIVATE).getString("jwt", " ")!!
            val response = getApiResponse("http://10.0.2.2:8832/groups/624/lessons/${date.toString()}", true, token)
            return response
        }

        override fun onPostExecute(response: String) {
            super.onPostExecute(response)

            val lessonContainer: Array<Lesson> = gson.fromJson(response, Array<Lesson>::class.java)
            val noLessonTV = findViewById<TextView>(R.id.no_lessons_tv)
            val mapOfLists = sortLessonsByDay(lessonContainer)
            val recyclerView = findViewById<RecyclerView>(R.id.schedule_list)
            if(lessonContainer.isEmpty()){
                noLessonTV.visibility = View.VISIBLE
            }
            recyclerView.layoutManager = LinearLayoutManager(mContext)
            recyclerView.adapter = ScheduleRecyclerViewAdapter(mapOfLists.first, mapOfLists.second)
            val currentWeekDates = findViewById<TextView>(R.id.current_week_dates)
            currentWeekDates.text = "${mapOfLists.second}–${mapOfLists.second.plusDays(6)}"
        }
    }

    private fun sortLessonsByDay(lessonContainer: Array<Lesson>): Pair<Map<String, List<Lesson>>, LocalDate> {
        val result = mutableMapOf<String, MutableList<Lesson>>()
        if(lessonContainer.isEmpty()) return  Pair(result, date)
        var weekDay: LocalDate = LocalDate.parse(lessonContainer[0].lessonDate)
        var monday: LocalDate = weekDay
        while (monday.getDayOfWeek() != DayOfWeek.MONDAY) {
            monday = monday.minusDays(1)
        }
        var sunday = weekDay
        while (sunday.getDayOfWeek() != DayOfWeek.SUNDAY) {
            sunday = sunday.plusDays(1)
        }
        weekDay = monday
        repeat(7){
            result[weekDay.toString()] = mutableListOf<Lesson>()
            weekDay = weekDay.plusDays(1)
        }
        val lessonContainerSorted = lessonContainer.sortedBy {it.timeStart}
        for (lesson in lessonContainerSorted) {
            val tempList = result.getOrPut(lesson.lessonDate) { mutableListOf() }
            tempList.add(lesson)
        }

        return Pair(result, monday)
    }
}