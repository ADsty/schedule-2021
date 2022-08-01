package com.kspt.db.schedule2021

import com.prolificinteractive.materialcalendarview.CalendarDay

/**
 * A data class that describes the information needed to display events in UI.
 * @param date is the date on which event occurs
 * @param subject is the subject on which you need to perform the work until event
 * @param action is the work which you need to do until event
 */
data class EventData(val date: String, val subject: String, val action: String) {

    fun dateToCalendarDay(): CalendarDay {
        val parsedDate = date.split(".")
        return CalendarDay.from(parsedDate[2].toInt(), parsedDate[1].toInt(), parsedDate[0].toInt())
    }
}
