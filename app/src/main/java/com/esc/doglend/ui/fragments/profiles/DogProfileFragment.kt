package com.esc.doglend.ui.fragments.profiles

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esc.doglend.R
import com.esc.doglend.databinding.DogProfileFragmentBinding
import com.esc.doglend.utils.calendar.*
import com.esc.doglend.utils.calendar.data.Available
import com.esc.doglend.utils.calendar.data.MonthDay
import com.esc.doglend.utils.calendar.data.getMonthName
import com.esc.doglend.viewmodels.DogProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class DogProfileFragment: Fragment(R.layout.dog_profile_fragment),
    WeekViewAdapter.OnDayAvailableClickedListener, MonthViewAdapter.OnDayClickedListener {

    private lateinit var binding: DogProfileFragmentBinding
    private val viewModel: DogProfileViewModel by viewModels()
    private lateinit var weekViewAdapter: WeekViewAdapter
    private lateinit var monthViewAdapter: MonthViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DogProfileFragmentBinding.bind(view)
        setListeners()
        setObservers()
        setWeekNames()
        setMonthView()
    }

    private fun setMonthView() {
        binding.weekView.apply {
            binding.weekView.layoutManager = getGridLayoutManager(7)
            monthViewAdapter = MonthViewAdapter(requireActivity().application, this@DogProfileFragment)
            adapter = monthViewAdapter
        }
    }

    private fun setWeekView() {
        binding.weekView.apply {
            binding.weekView.layoutManager = getGridLayoutManager(8)
            weekViewAdapter = WeekViewAdapter(this@DogProfileFragment)
            adapter = weekViewAdapter
        }
    }

    private fun getGridLayoutManager(numColumns: Int) =
        GridLayoutManager(requireContext(), numColumns, RecyclerView.VERTICAL, false)

    private fun setObservers() {
        viewModel.getAvailability().observe(viewLifecycleOwner, {
            weekViewAdapter.submitList(it)
        })
        viewModel.getMonthDays().observe(viewLifecycleOwner, {
            monthViewAdapter.submitList(it.toMutableList())
            binding.monthHeader.currentMonth.dayText.text = getMonthName(it[15].year, it[15].month)
        })
    }

    private fun setListeners() {
        binding.fab.setOnClickListener {
            when (binding.fabText.text) {
                resources.getString(R.string.edit) -> {
                    binding.fabText.text = resources.getString(R.string.save)
                    editAvailability()
                }
                resources.getString(R.string.save) -> {
                    binding.fabText.text = resources.getString(R.string.edit)
                    viewCalendar()
                    //viewModel.saveAvailability()
                }
            }
        }
    }

    private fun setWeekNames() {
        binding.weekNames.apply {
            mon.dayText.text = resources.getString(R.string.monday)
            tue.dayText.text = resources.getString(R.string.tuesday)
            wed.dayText.text = resources.getString(R.string.wednesday)
            thu.dayText.text = resources.getString(R.string.thursday)
            fri.dayText.text = resources.getString(R.string.friday)
            sat.dayText.text = resources.getString(R.string.saturday)
            sun.dayText.text = resources.getString(R.string.sunday)
        }
        binding.monthHeader.nextMonth.dayText.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.baseline_expand_less_24)
            binding.monthHeader.nextMonth.root.setOnClickListener { viewModel.addPreviousMonth() }
        binding.monthHeader.previousMonth.dayText.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.baseline_expand_more_24)
        binding.monthHeader.previousMonth.root.setOnClickListener { viewModel.addNextMonth() }
    }

    private fun editAvailability() {
        binding.root.transitionToEnd()
        viewModel.setupAvailability()
        if (binding.timeHeader.dayText.text == "")
            binding.timeHeader.dayText.text = resources.getString(R.string.time)
        if (this::weekViewAdapter.isInitialized) {
            binding.weekView.layoutManager = getGridLayoutManager(8)
            binding.weekView.adapter = weekViewAdapter
        } else setWeekView()

    }
    private fun viewCalendar() {
        binding.root.transitionToStart()
        binding.weekView.layoutManager = getGridLayoutManager(7)
        binding.weekView.adapter = monthViewAdapter
    }

    override fun onItemClick(available: Available) {
        val item: Available =
            if (available.available) Available(available.day, available.time, available.position, false)
        else Available(available.day, available.time, available.position, true)
        viewModel.updateAvailability(item)
    }

    override fun onItemClick(monthDay: MonthDay) {
        //manage booking
    }

}