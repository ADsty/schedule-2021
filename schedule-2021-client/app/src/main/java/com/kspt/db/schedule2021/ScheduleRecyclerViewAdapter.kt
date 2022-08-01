package com.kspt.db.schedule2021
import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.schedule_event.view.*
import java.time.LocalDate

class ScheduleRecyclerViewAdapter(private val items: Map<String, List<Lesson>>, private val monday: LocalDate) :
    RecyclerView.Adapter<ScheduleRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener = View.OnClickListener { v ->
        val data = v.tag as Lesson
        Log.d(
            "onClick (once)",
            data.lessonDate + " " + data.timeStart + " " + data.timeEnd + " " + data.subjectName
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.schedule_event, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentData = monday.plusDays(position.toLong())
        val data: List<Lesson>? = items[currentData.toString()]
        if (data == null || data.isEmpty()){
            holder.cardView.visibility=View.GONE
            holder.constraintLayout.visibility=View.GONE
            for(i in 0 until 6){
                holder.lessonNames[i].visibility=View.GONE
                holder.lessonPlaces[i].visibility=View.GONE
                holder.lessonTimes[i].visibility=View.GONE
                holder.lessonTypes[i].visibility=View.GONE
                holder.teacherNames[i].visibility=View.GONE
            }
        }
        else {
            for(i in 0..data.size - 1) {
                holder.lessonNames[i].text = data[i].subjectName
                holder.lessonPlaces[i].text = data[i].place
                holder.lessonTimes[i].text = data[i].timeStart + "-" + data[i].timeEnd
                holder.lessonTypes[i].text = data[i].type
                holder.teacherNames[i].text = data[i].teacherList[0].name
            }
            for(i in data.size until 6){
                holder.lessonNames[i].visibility=View.GONE
                holder.lessonPlaces[i].visibility=View.GONE
                holder.lessonTimes[i].visibility=View.GONE
                holder.lessonTypes[i].visibility=View.GONE
                holder.teacherNames[i].visibility=View.GONE
            }
            holder.dayOfWeek.text = currentData.dayOfWeek.toString()
            holder.date.text = currentData.toString()
            with(holder.view) {
                tag = data
                setOnClickListener(mOnClickListener)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val lessonNames: List<TextView> = mutableListOf<TextView>(view.lessonName1, view.lessonName2, view.lessonName3,
            view.lessonName4, view.lessonName5, view.lessonName6)
        val lessonPlaces: List<TextView> = mutableListOf<TextView>(view.lessonPlace1, view.lessonPlace2, view.lessonPlace3,
            view.lessonPlace4, view.lessonPlace5, view.lessonPlace6)
        val lessonTimes: List<TextView> = mutableListOf<TextView>(view.lessonTime1, view.lessonTime2, view.lessonTime3,
            view.lessonTime4, view.lessonTime5, view.lessonTime6)
        val lessonTypes: List<TextView> = mutableListOf<TextView>(view.lessonType1, view.lessonType2, view.lessonType3,
            view.lessonType4, view.lessonType5, view.lessonType6)
        val teacherNames: List<TextView> = mutableListOf<TextView>(view.teacherName1, view.teacherName2, view.teacherName3,
            view.teacherName4, view.teacherName5, view.teacherName6)
        val dayOfWeek: TextView = view.day_of_week
        val date: TextView = view.schedule_date
        val cardView: CardView = view.card_view
        val constraintLayout: ConstraintLayout = view.constraintLayout
    }
}