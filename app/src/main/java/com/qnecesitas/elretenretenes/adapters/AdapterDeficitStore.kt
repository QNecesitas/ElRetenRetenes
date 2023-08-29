package com.qnecesitas.elretenretenes.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qnecesitas.elretenretenes.databinding.RecyclerDeficitBinding
import com.qnecesitas.elretenretenes.adapters.AdapterDeficitStore.*
import com.qnecesitas.elretenretenes.data.Store

class AdapterDeficitStore :
    ListAdapter<Store , DeficitViewHolder>(DiffCallback) {

    private var clickEntry: ITouchEntry? = null


    class DeficitViewHolder(private var binding: RecyclerDeficitBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SimpleDateFormat")
        fun bind(
            store: Store ,
            clickEntry: ITouchEntry?
        ) {

            //Declare
            val amount = store.amount
            val size = store.size
            val location = store.location
            binding.tvEntry.setOnClickListener { clickEntry?.onClickEntry(store) }




            binding.tvSize.text = size
            binding.tvAmount.text = amount.toString()
            binding.tvLocation.text = location

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
        private val DiffCallback = object : DiffUtil.ItemCallback<Store>() {
            override fun areItemsTheSame(oldItem: Store , newItem: Store): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Store , newItem: Store): Boolean {
                return oldItem == newItem
            }

        }
    }

    interface ITouchEntry {
        fun onClickEntry(store: Store)
    }

    fun setClickEntry(clickEntry: ITouchEntry?) {
        this.clickEntry = clickEntry
    }


}