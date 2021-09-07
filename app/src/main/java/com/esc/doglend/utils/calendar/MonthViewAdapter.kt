package com.esc.doglend.utils.calendar

import android.app.Application
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.esc.doglend.R
import com.esc.doglend.databinding.WeekdayButtonBinding
import com.esc.doglend.utils.calendar.data.MonthDay

class MonthViewAdapter(private val application: Application, private val listener: OnDayClickedListener):
    ListAdapter<MonthDay, MonthViewAdapter.DayListViewHolder>(DiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayListViewHolder {
        val binding = WeekdayButtonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayListViewHolder, pos: Int) {
        if (!getItem(pos).currentMonth) holder.bindOtherDays()
        else if (getItem(pos).booking != null) holder.bindBookedDays()
        else holder.bindDays()
    }

    inner class DayListViewHolder(private val binding: WeekdayButtonBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindOtherDays() {
            binding.dayText.setTextColor(Color.GRAY)
            binding.root.setOnClickListener(null)
            setDayNumber()
        }
        fun bindBookedDays() {
            binding.dayText.background = ContextCompat.getDrawable(application, R.drawable.amber_circle)
            binding.dayText.setTextColor(Color.WHITE)
            setDayNumber()
            setOnClick()
        }
        fun bindDays() {
            binding.dayText.setTextColor(Color.WHITE)
            setDayNumber()
            setOnClick()
        }
        private fun setDayNumber() {
            val day = getItem(bindingAdapterPosition)
            binding.dayText.text = day.day.toString()
        }
        private fun setOnClick() {
            val day = getItem(bindingAdapterPosition)
            binding.root.setOnClickListener {
                listener.onItemClick(day)
            }
        }
    }

    interface OnDayClickedListener  {
        fun onItemClick(monthDay: MonthDay)
    }

    class DiffCallback: DiffUtil.ItemCallback<MonthDay>() {
        override fun areItemsTheSame(oldItem: MonthDay, newItem: MonthDay) =
            (oldItem.booking == newItem.booking && oldItem.month == newItem.month)

        override fun areContentsTheSame(oldItem: MonthDay, newItem: MonthDay) =
            oldItem == newItem
    }
}