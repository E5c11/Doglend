package com.esc.doglend.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.esc.doglend.databinding.DogItemBinding
import com.esc.doglend.entities.Dog

class DogListAdapter(private val dogs: List<Dog>, private val listener: OnItemClickedListener):
    RecyclerView.Adapter<DogListAdapter.DogListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogListViewHolder {
        val binding = DogItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DogListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DogListViewHolder, position: Int) {
        holder.bind(dogs[position])
    }

    override fun getItemCount(): Int {
        return dogs.size
    }

    inner class DogListViewHolder(private val binding: DogItemBinding): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val dog = dogs[position]
                    listener.onItemClick(dog)
                }
            }
        }

        fun bind(dog: Dog) { binding.dogName.text = dog.name }
    }

    interface OnItemClickedListener  {
        fun onItemClick(dog: Dog)
    }
}