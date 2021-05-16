package com.mapo.walkaholic.ui.main.dashboard.calendar.decorator

import android.content.Context
import com.mapo.walkaholic.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class SeletedDayDecorator(context: Context) : DayViewDecorator {
    val drawable = context.resources.getDrawable(R.drawable.selector_dashboard_calendar)

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return true
    }

    override fun decorate(view: DayViewFacade?) {
        view?.setSelectionDrawable(drawable)
    }
}