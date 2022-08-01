package com.kspt.db.schedule2021

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.event.view.*

class RecyclerViewAdapter(private val items: List<Deadline>, context: Context, token: String) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    private val mContext = context
    private val mOnClickListener: View.OnClickListener
    private val mToken = token

    //TODO где-то здесь добавить интенты с нажатия на дедлайны?
    init {
        mOnClickListener = View.OnClickListener { v ->

            val data = v.tag as Deadline
            val dInfoIntent = Intent(context, DeadlineInfoActivity::class.java)
            dInfoIntent.putExtra("date", data.deadlineDate)
            dInfoIntent.putExtra("time", data.notificationTime)
            dInfoIntent.putExtra("subject", data.lesson.subjectName)
            dInfoIntent.putExtra("description", data.description)
            dInfoIntent.putExtra("title", data.title)
            context.startActivity(dInfoIntent)
//            Log.d("onClick (once)", data.deadlineDate + " " + data.lesson.subjectName + " " + data.description)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = items[position]
        holder.date.text = data.deadlineDate
        holder.subject.text = data.subjectName
        holder.action.text = data.description
        holder.deleteButton.setOnClickListener{
            JsonTask(mContext, data.id, mToken).execute()
        }
        with(holder.view) {
            tag = data
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val date: TextView = view.date
        val subject: TextView = view.subject
        val action: TextView = view.action
        val deleteButton: ImageButton = view.btn
    }

    private inner class JsonTask internal constructor(context: Context, deadlineId: Long, token: String): AsyncTask<String, String, String>() {

        private val mContext = context
        private val deadlineID = deadlineId
        private val mToken = token
        override fun doInBackground(vararg params: String): String? {

            val response = deleteDeadline(deadlineID, mToken)
            return response
        }

        override fun onPostExecute(response: String) {
            super.onPostExecute(response)
            val intent = Intent(mContext, DeadlineActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            mContext.startActivity(intent)
        }
    }
}