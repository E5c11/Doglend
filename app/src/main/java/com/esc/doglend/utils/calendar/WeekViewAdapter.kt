package com.esc.doglend.utils.calendar

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.esc.doglend.databinding.WeekdayButtonBinding
import com.esc.doglend.utils.calendar.data.Available
import com.esc.doglend.utils.calendar.data.WeekDays

class WeekViewAdapter(private val listener: OnDayAvailableClickedListener):
    ListAdapter<Available, WeekViewAdapter.DayListViewHolder>(DiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayListViewHolder {
        val binding = WeekdayButtonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayListViewHolder, pos: Int) {
        if (getItem(pos).day == WeekDays.TIME) holder.bindTime(getItem(pos))
        else holder.bindDays(getItem(pos))
    }

    inner class DayListViewHolder(private val binding: WeekdayButtonBinding): RecyclerView.ViewHolder(binding.root) {

        fun bindTime(available: Available) {
            binding.dayText.text = available.time.toString() + ":00"
            binding.root.setCardBackgroundColor(Color.BLACK)
            binding.root.setOnClickListener(null)
        }
        fun bindDays(available: Available) {
            if (available.available) binding.root.setCardBackgroundColor(Color.GREEN)
            else binding.root.setCardBackgroundColor(Color.RED)
            binding.dayText.text = ""
            setOnClick()
        }
        private fun setOnClick() {
            binding.root.setOnClickListener {
                val pos = bindingAdapterPosition
                val day = getItem(pos)
                if (day.available) binding.root.setCardBackgroundColor(Color.RED)
                else binding.root.setCardBackgroundColor(Color.GREEN)
                listener.onItemClick(day)
            }
        }
    }

    interface OnDayAvailableClickedListener  {
        fun onItemClick(available: Available)
    }

    class DiffCallback: DiffUtil.ItemCallback<Available>() {
        override fun areItemsTheSame(oldItem: Available, newItem: Available) =
            (oldItem.day == newItem.day && oldItem.time == newItem.time)

        override fun areContentsTheSame(oldItem: Available, newItem: Available) =
            oldItem == newItem

    }
}