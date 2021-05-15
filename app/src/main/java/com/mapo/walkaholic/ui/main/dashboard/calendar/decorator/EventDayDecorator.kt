package com.mapo.walkaholic.ui.main.dashboard.calendar.decorator

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import com.mapo.walkaholic.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import java.security.AccessControlContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

class EventDayDecorator(context: Context, var dates: Collection<CalendarDay?>) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        Log.e("date", dates.toString())
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(DotSpan(8f, Color.parseColor("#F9A25B")))
    }
}