package com.qnecesitas.elretenretenes.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qnecesitas.elretenretenes.R
import com.qnecesitas.elretenretenes.data.Store
import com.qnecesitas.elretenretenes.adapters.AdapterStore.*
import com.qnecesitas.elretenretenes.databinding.RecyclerStoreBinding

class AdapterStore(private val context: Context) :
    ListAdapter<Store ,StoreViewHolder>(DiffCallback) {


    private var clickEdit: ITouchEdit? = null
    private var clickDelete: ITouchDelete? = null
    private var clickSales: ITouchSales? = null
    private var clickAmount: ITouchAmount? = null
    private var clickLocation: ITouchLocation? =null
    private var clickEntry:ITouchEntry?=null


    class StoreViewHolder(private var binding: RecyclerStoreBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SimpleDateFormat")
        fun bind(
            store:Store,
            context: Context ,
            clickEdit: ITouchEdit? ,
            clickDelete: ITouchDelete? ,
            clickSales: ITouchSales? ,
            clickAmount: ITouchAmount? ,
            clickLocation: ITouchLocation?,
            clickEntry:ITouchEntry?
        ) {

            //Declare
            val amount =store.amount
            val size = store.size



            binding.tvSize.text = size
            binding.tvAmount.text = amount.toString()
            binding.menuOption.setOnClickListener {
                val popupMenu = PopupMenu(context, binding.menuOption)
                popupMenu.menuInflater.inflate(R.menu.menu_option_reten, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_option_reten_edit -> {
                            clickEdit?.onClickEdit(store)

                        }

                        R.id.menu_option_reten_delete -> {
                            clickDelete?.onClickDelete(store)
                        }

                        R.id.menu_option_reten_sales -> {
                            clickSales?.onClickSales(store)
                        }

                        R.id.menu_option_reten_amount -> {
                            clickAmount?.onClickAmount(store)
                        }

                        R.id.menu_option_reten_location ->{
                            clickLocation?.onClickLocation(store)
                        }
                        R.id.menu_option_reten_entry ->{
                            clickEntry?.onClickEntry(store)
                        }
                    }
                    false
                }
                popupMenu.show()

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): StoreViewHolder {
        val viewHolder = StoreViewHolder(
            RecyclerStoreBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
        }
        return viewHolder
    }


    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        holder.bind(
            getItem(position), context,
            clickEdit, clickDelete, clickSales, clickAmount,clickLocation,clickEntry
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

    //Details
    interface ITouchEdit {
        fun onClickEdit(store: Store)
    }

    fun setClickEdit(clickEdit: ITouchEdit?) {
        this.clickEdit = clickEdit
    }

    interface ITouchDelete {
        fun onClickDelete(store: Store)
    }

    fun setClickDelete(clickDelete: ITouchDelete?) {
        this.clickDelete = clickDelete
    }

    interface ITouchSales {
        fun onClickSales(store: Store)
    }

    fun setClickSales(clickSales: ITouchSales?) {
        this.clickSales = clickSales
    }

    interface ITouchAmount {
        fun onClickAmount(store: Store)
    }

    fun setClickAmount(clickAmount: ITouchAmount?) {
        this.clickAmount = clickAmount
    }

    interface ITouchLocation {
        fun onClickLocation(store: Store)
    }

    fun setClickLocation(clickLocation: ITouchLocation?) {
        this.clickLocation = clickLocation
    }

    interface ITouchEntry {
        fun onClickEntry(store: Store)
    }

    fun setClickEntry(clickEntry: ITouchEntry?) {
        this.clickEntry = clickEntry
    }



}