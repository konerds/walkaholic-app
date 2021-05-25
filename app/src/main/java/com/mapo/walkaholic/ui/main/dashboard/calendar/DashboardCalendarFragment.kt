package com.mapo.walkaholic.ui.main.dashboard.calendar

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentDashboardCalendarBinding
import com.mapo.walkaholic.ui.alertDialog
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.confirmDialog
import com.mapo.walkaholic.ui.handleApiError
import com.mapo.walkaholic.ui.main.dashboard.calendar.decorator.CalendarDayDecorator
import com.mapo.walkaholic.ui.main.dashboard.calendar.decorator.EventDayDecorator
import com.mapo.walkaholic.ui.main.dashboard.calendar.decorator.SeletedDayDecorator
import com.mapo.walkaholic.ui.main.dashboard.calendar.decorator.TodayDecorator
import com.mapo.walkaholic.ui.notCancelableConfirmDialog
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.format.TitleFormatter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

class DashboardCalendarFragment :
    BaseFragment<DashboardCalendarViewModel, FragmentDashboardCalendarBinding, MainRepository>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val startTimeCalendar = Calendar.getInstance()
        val endTimeCalendar = Calendar.getInstance()
        val currentYear = startTimeCalendar.get(Calendar.YEAR)
        var currentMonth = startTimeCalendar.get(Calendar.MONTH)
        // 오늘 날짜 선택된 상태로
        val currentTime = Calendar.getInstance().time
        binding.calendarView.selectedDate = CalendarDay.today()
        binding.textView.text = SimpleDateFormat(
            "MM월dd일, EE요일",
            Locale.KOREAN
        ).format(currentTime)
        // 기록 날짜 표시
        val calendarDays = arrayListOf<CalendarDay?>()
        // 달력 범위 지정
        binding.calendarView.state().edit()
            .setFirstDayOfWeek(Calendar.SUNDAY)
            .setMinimumDate(CalendarDay.from(currentYear - 20, 1, 1))
            .setMaximumDate(
                CalendarDay.from(
                    currentYear + 20, 12, endTimeCalendar.getActualMaximum(
                        Calendar.DAY_OF_MONTH
                    )
                )
            )
            .setCalendarDisplayMode(CalendarMode.MONTHS)
            .commit()
        // custom decorations
        binding.calendarView.addDecorators(
            CalendarDayDecorator(),
            TodayDecorator(),
            SeletedDayDecorator(requireContext())
        )
        // 달에 따라 4, 5, 6주 변동 처리
        binding.calendarView.isDynamicHeightEnabled = true
        // 타이틀 일자 형식
        binding.calendarView.setTitleFormatter(TitleFormatter {
            val simpleDateFormat = SimpleDateFormat("yyyy년 MM월", Locale.KOREAN)
            simpleDateFormat.format(startTimeCalendar.time)
        })
        viewModel.getUser()
        viewModel.userResponse.observe(viewLifecycleOwner, Observer { _userResponse ->
            when (_userResponse) {
                is Resource.Success -> {
                    when (_userResponse.value.code) {
                        "200" -> {
                            // 날짜 선택 리스너
                            binding.calendarView.setOnDateChangedListener { widget, date, selected ->
                                binding.textView.text = SimpleDateFormat(
                                    "MM월dd일, EE요일",
                                    Locale.KOREAN
                                ).format(date.date)
                                binding.dashCalendarTvTotalDate.text = SimpleDateFormat(
                                    "yyyy.MM.dd EE요일,",
                                    Locale.KOREAN
                                ).format(date.date)
                                viewModel.getWalkRecord(
                                    _userResponse.value.data.first().id,
                                    SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN).format(date.date)
                                )
                            }
                            // 월 선택 리스너
                            binding.calendarView.setOnMonthChangedListener { _monthListenerWidget, _monthListenerDate ->
                                currentMonth = _monthListenerDate.month
                            }
                            viewModel.getWalkRecord(
                                _userResponse.value.data.first().id,
                                SimpleDateFormat(
                                    "yyyy-MM-dd",
                                    Locale.KOREAN
                                ).format(CalendarDay.today().date)
                            )
                            viewModel.calendarResponse.observe(
                                viewLifecycleOwner,
                                Observer { _calendarResponse ->
                                    when (_calendarResponse) {
                                        is Resource.Success -> {
                                            when (_calendarResponse.value.code) {
                                                "200" -> {
                                                    binding.dashCalendarRV.also { _calendarRV ->
                                                        _calendarRV.layoutManager = LinearLayoutManager(requireContext())
                                                        _calendarRV.setHasFixedSize(true)

                                                        binding.dashCalendarTvTotalTime.text =
                                                            _calendarResponse.value.totalRecord.totalWalkTime
                                                        binding.dashCalendarTvTotalDistance.text =
                                                            _calendarResponse.value.totalRecord.totalDistance
                                                        binding.dashCalendarTvCalorie.text =
                                                            _calendarResponse.value.totalRecord.totalWalkCalorie
                                                        binding.dashCalendarTvWalkAmount.text =
                                                            _calendarResponse.value.totalRecord.totalWalkCount

                                                        Log.e("기록", _calendarResponse.value.data.size.toString())
                                                        if(_calendarResponse.value.data.size != 0) {
                                                            binding.dashCalendarRV.adapter =
                                                                _calendarResponse.value.data.let { _walkRecord ->
                                                                    DashboardCalendarAdapter(_walkRecord)
                                                                }
                                                        }
                                                        else {
                                                            binding.dashCalendarRV.adapter = null
                                                        }
                                                    }
                                                }
                                                else -> {
                                                    // Error
                                                    binding.dashCalendarTvTotalTime.text = "오류"
                                                    binding.dashCalendarTvTotalDistance.text =
                                                        "오류"
                                                    binding.dashCalendarTvCalorie.text = "오류"
                                                    binding.dashCalendarTvWalkAmount.text = "오류"
                                                    confirmDialog(
                                                        _calendarResponse.value.message,
                                                        {
                                                            viewModel.getWalkRecord(
                                                                _userResponse.value.data.first().id,
                                                                SimpleDateFormat(
                                                                    "yyyy-MM-dd",
                                                                    Locale.KOREAN
                                                                ).format(CalendarDay.today().date)
                                                            )
                                                        },
                                                        "재시도"
                                                    )
                                                }
                                            }
                                        }
                                        is Resource.Loading -> {
                                            // Loading
                                        }
                                        is Resource.Failure -> {
                                            // Network Error
                                            handleApiError(_calendarResponse) {
                                                viewModel.getWalkRecord(
                                                    _userResponse.value.data.first().id,
                                                    SimpleDateFormat(
                                                        "yyyy-MM-dd",
                                                        Locale.KOREAN
                                                    ).format(CalendarDay.today().date)
                                                )
                                            }
                                        }
                                    }
                                })
                            viewModel.getCalendarMonth(
                                _userResponse.value.data.first().id,
                                "$currentYear-" + if (currentMonth in 1..9) {
                                    "0${currentMonth + 1}"
                                } else {
                                    (currentMonth + 1).toString()
                                }
                            )
                            viewModel.calendarMonthResponse.observe(
                                viewLifecycleOwner,
                                Observer { _calendarMonthResponse ->
                                    when (_calendarMonthResponse) {
                                        is Resource.Success -> {
                                            when (_calendarMonthResponse.value.code) {
                                                "200" -> {
                                                    _calendarMonthResponse.value.data.forEachIndexed { index, walkRecord ->
                                                        calendarDays.add(walkRecord.year.let { _year ->
                                                            walkRecord.month.let { _month ->
                                                                walkRecord.date.let { _date ->
                                                                    CalendarDay.from(
                                                                        _year.toInt(),
                                                                        _month.toInt() - 1,
                                                                        _date.toInt()
                                                                    )
                                                                }
                                                            }
                                                        })
                                                        binding.calendarView.addDecorator(
                                                            EventDayDecorator(
                                                                requireContext(),
                                                                calendarDays
                                                            )
                                                        )
                                                    }
                                                }
                                                else -> {
                                                    // Error
                                                    confirmDialog(
                                                        _calendarMonthResponse.value.message,
                                                        {
                                                            viewModel.getCalendarMonth(
                                                                _userResponse.value.data.first().id,
                                                                "$currentYear-" + if (currentMonth in 1..9) {
                                                                    "0${currentMonth + 1}"
                                                                } else {
                                                                    (currentMonth + 1).toString()
                                                                }
                                                            )
                                                        },
                                                        "재시도"
                                                    )
                                                }
                                            }
                                        }
                                        is Resource.Loading -> {
                                            // Loading
                                        }
                                        is Resource.Failure -> {
                                            // Network Error
                                            handleApiError(_calendarMonthResponse) {
                                                viewModel.getCalendarMonth(
                                                    _userResponse.value.data.first().id,
                                                    "$currentYear-" + if (currentMonth in 1..9) {
                                                        "0${currentMonth + 1}"
                                                    } else {
                                                        (currentMonth + 1).toString()
                                                    }
                                                )
                                            }
                                        }
                                    }
                                })
                        }
                        else -> {
                            // Error
                            confirmDialog(
                                _userResponse.value.message,
                                {
                                    viewModel.getUser()
                                },
                                "재시도"
                            )
                        }
                    }
                }
                is Resource.Loading -> {
                    // Loading
                }
                is Resource.Failure -> {
                    // Network Error
                    handleApiError(_userResponse) { viewModel.getUser() }
                }
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val navDirection: NavDirections? =
                    DashboardCalendarFragmentDirections.actionActionBnvDashWalkRecordToActionBnvDash()
                if (navDirection != null) {
                    findNavController().navigate(navDirection)
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
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