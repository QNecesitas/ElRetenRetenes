package com.qnecesitas.elretenretenes.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qnecesitas.elretenretenes.data.Counter
import com.qnecesitas.elretenretenes.databinding.RecyclerDeficitBinding
import com.qnecesitas.elretenretenes.adapters.AdapterDeficit.*

class AdapterDeficit :
    ListAdapter<Counter , DeficitViewHolder>(DiffCallback) {

    private var clickEntry: ITouchEntry? = null


    class DeficitViewHolder(private var binding: RecyclerDeficitBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SimpleDateFormat")
        fun bind(
            counter: Counter ,
            clickEntry: ITouchEntry?
        ) {

            //Declare
            val amount = counter.amount
            val size = counter.size
            val location = counter.location




            binding.tvSize.text = size
            binding.tvAmount.text = amount.toString()
            binding.tvLocation.text = location
            binding.tvEntry.setOnClickListener { clickEntry?.onClickEntry(counter) }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): DeficitViewHolder {
        val viewHolder = DeficitViewHolder(
            RecyclerDeficitBinding.inflate(
                LayoutInflater.from(parent.context) ,
                parent ,
                false
            )
        )
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
        }
        return viewHolder
    }


    override fun onBindViewHolder(holder: DeficitViewHolder , position: Int) {
        holder.bind(
            getItem(position) ,
            clickEntry
        )
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Counter>() {
            override fun areItemsTheSame(oldItem: Counter , newItem: Counter): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Counter , newItem: Counter): Boolean {
                return oldItem == newItem
            }

        }
    }

    interface ITouchEntry {
        fun onClickEntry(counter: Counter)
    }

    fun setClickEntry(clickEntry: ITouchEntry?) {
        this.clickEntry = clickEntry
    }


}