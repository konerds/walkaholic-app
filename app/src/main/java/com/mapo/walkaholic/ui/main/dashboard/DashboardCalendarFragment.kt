package com.mapo.walkaholic.ui.main.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.SgisApi
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentDashboardCalendarBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.handleApiError
import com.mapo.walkaholic.ui.main.dashboard.calendar.CalendarDayDecorator
import com.mapo.walkaholic.ui.main.dashboard.calendar.EventDayDecorator
import com.mapo.walkaholic.ui.main.dashboard.calendar.SeletedDayDecorator
import com.mapo.walkaholic.ui.main.dashboard.calendar.TodayDecorator
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DashboardCalendarFragment :
        BaseFragment<DashboardCalendarViewModel, FragmentDashboardCalendarBinding, MainRepository>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        val selectDay: DateFormat = SimpleDateFormat("yyyyMMddEE")

        var startTimeCalendar = Calendar.getInstance()
        var endTimeCalendar = Calendar.getInstance()

        val currentYear = startTimeCalendar.get(Calendar.YEAR)
        //val currentMonth = startTimeCalendar.get(Calendar.MONTH)
        //val currentDate = startTimeCalendar.get(Calendar.DATE)

        // 오늘 날짜 선택된 상태로
        binding.calendarView.setSelectedDate(CalendarDay.today())

        var currentTime = Calendar.getInstance().getTime()
        binding.textView.setText(SimpleDateFormat("MM월dd일, EE요일", Locale.KOREAN).format(currentTime))

        /*// 기록 날짜 표시
        var calendarDays = arrayListOf<CalendarDay?>()
        viewModel.calendarResponse.observe(viewLifecycleOwner,Observer { it5 ->
            when(it5) {
                is Resource.Success -> {
                    it5.value.walkRecord.forEachIndexed { index, walkRecord ->
                        var year: Int = (walkRecord.walk_date?.substring(0,4))?.toInt() ?: -1
                        var month: Int = (walkRecord.walk_date?.substring(4,6))?.toInt() ?: -1
                        var dayy: Int = (walkRecord.walk_date?.substring(6,8))?.toInt() ?: -1

                        calendarDays.add(CalendarDay.from(year, month-1, dayy))
                    }
                }
                is Resource.Loading -> {

                }
                is Resource.Failure -> {
                    handleApiError(it5)
                }
            }
        })*/

        var testDays = arrayListOf<String?>("20210510", "20210501", "20210512")
        var calendarDays = arrayListOf<CalendarDay?>()
        testDays.forEachIndexed{ index, testDays ->
            var year: Int = (testDays?.substring(0,4))?.toInt() ?: -1
            var month: Int = (testDays?.substring(4,6))?.toInt() ?: -1
            var dayy: Int = (testDays?.substring(6,8))?.toInt() ?: -1

            calendarDays.add(CalendarDay.from(year, month-1, dayy))
        }

        // 달력 범위 지정
        binding.calendarView.state().edit()
            .setFirstDayOfWeek(Calendar.SUNDAY)
            .setMinimumDate(CalendarDay.from(currentYear-20, 1, 1))
            .setMaximumDate(CalendarDay.from(currentYear+20, 12, endTimeCalendar.getActualMaximum(
                Calendar.DAY_OF_MONTH)))
            .setCalendarDisplayMode(CalendarMode.MONTHS)
            .commit()

        // custom decorations
        binding.calendarView.addDecorators(
            CalendarDayDecorator(),
            TodayDecorator(),
            SeletedDayDecorator(requireContext()),
            EventDayDecorator(requireContext(), calendarDays)
        )

        // 달에 따라 4, 5, 6주 변동 처리
        binding.calendarView.setDynamicHeightEnabled(true)

        // 날짜 선택 리스너
        binding.calendarView.setOnDateChangedListener { widget, date, selected ->
            binding.textView.setText(SimpleDateFormat("MM월dd일, EE요일", Locale.KOREAN).format(date.date))

            viewModel.userResponse.observe(viewLifecycleOwner, Observer {
                when(it) {
                    is Resource.Success -> {
                        viewModel.getCalendar(it.value.user.id, SimpleDateFormat("yyyyMMdd", Locale.KOREAN).format(date.date))

                        viewModel.calendarResponse.observe(viewLifecycleOwner, Observer { it2 ->
                            binding.dashCalendarRV.also { it3 ->
                                val linearLayoutManager =
                                    LinearLayoutManager(requireContext())
                                linearLayoutManager.orientation =
                                    LinearLayoutManager.VERTICAL
                                it3.layoutManager = linearLayoutManager
                                it3.setHasFixedSize(true)
                                when(it2) {
                                    is Resource.Success -> {
                                        if(!it2.value.error) {
                                            if(CalendarDay.today().date == date.date) {
                                                binding.dashCalendarTvIntroDate.text = "오늘의 총 산책기록이에요"
                                                binding.dashCalendarTvDetailRecord.text = "오늘의 세부 산책기록이에요"
                                            } else {
                                                binding.dashCalendarTvIntroDate.text = SimpleDateFormat("yyyy.MM.dd EE요일", Locale.KOREAN).format(date.date) + "의 총 산책기록이에요"
                                                binding.dashCalendarTvDetailRecord.text = SimpleDateFormat("yyyy.MM.dd EE요일", Locale.KOREAN).format(date.date) + "의 세부 산책기록이에요"
                                            }
                                            it3.adapter = it2.value.walkRecord?.let { it4 ->
                                                DashboardCalendarAdapter(it4)
                                            }
                                            binding.dashCalendarTvTotalDate.text = SimpleDateFormat("yyyy.MM.dd EE요일,", Locale.KOREAN).format(date.date)
                                            var totalTime = 0
                                            var totalDistance = 0
                                            var totalCalorie = 0
                                            var totalWalkAmount = 0
                                            it2.value.walkRecord.forEachIndexed { index, walkRecord ->
                                                totalTime += walkRecord.walk_time?.toInt() ?: -1
                                                totalDistance += walkRecord.walk_distance?.toInt()
                                                    ?: -1
                                                totalCalorie += walkRecord.walk_calorie?.toInt() ?: -1
                                            }
                                            binding.dashCalendarTvTotalTime.text = totalTime.toString()
                                            binding.dashCalendarTvTotalDistance.text = totalDistance.toString()
                                            binding.dashCalendarTvCalorie.text = totalCalorie.toString()
                                            binding.dashCalendarTvWalkAmount.text = "200"
                                        }
                                    }
                                    is Resource.Loading -> {

                                    }
                                    is Resource.Failure -> {
                                        handleApiError(it2)
                                    }
                                }
                            }
                        })
                    }
                    is Resource.Loading -> {

                    }
                    is Resource.Failure -> {
                        handleApiError(it)
                    }
                }
            })
        }
        viewModel.getUser()
    }

    override fun getViewModel() = DashboardCalendarViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentDashboardCalendarBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val accessToken = runBlocking { userPreferences.jwtToken.first() }
        val api = remoteDataSource.buildRetrofitInnerApi(InnerApi::class.java, accessToken)
        val apiWeather = remoteDataSource.buildRetrofitApiWeatherAPI(ApisApi::class.java)
        val apiSGIS = remoteDataSource.buildRetrofitApiSGISAPI(SgisApi::class.java)
        return MainRepository(api, apiWeather, apiSGIS, userPreferences)
    }
}