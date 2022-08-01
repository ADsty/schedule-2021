package com.kspt.db.schedule2021

import android.content.SharedPreferences
import java.util.*

/**
 * Object to get data for app from external places such as databases (need to implement it in future)
 */
object GetData {

    private val items: MutableList<Deadline> = ArrayList()

    fun addItems(token: String?) {
        if (token == null) return
        println(token)
        val response = getApiResponse("http://10.0.2.2:8832/students/1/deadlines", true, token)
        val deadlineContainer: Array<Deadline> = gson.fromJson(response, Array<Deadline>::class.java)
        for (deadline: Deadline in deadlineContainer) {
            items.add(deadline)
            println(items.size)
        }

    }

    fun getItems(): MutableList<Deadline> {
        return items
    }

}
