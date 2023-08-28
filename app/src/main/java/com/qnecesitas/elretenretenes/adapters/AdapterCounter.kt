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
import com.qnecesitas.elretenretenes.data.Counter
import com.qnecesitas.elretenretenes.databinding.RecyclerCounterBinding
import com.qnecesitas.elretenretenes.adapters.CounterAdapter.*

class CounterAdapter(private val context: Context) :
    ListAdapter<Counter , CounterViewHolder>(DiffCallback) {


    private var clickEdit: ITouchEdit? = null
    private var clickDelete: ITouchDelete? = null
    private var clickSales: ITouchSales? = null
    private var clickAmount: ITouchAmount? = null
    private var clickLocation: ITouchLocation? = null
    private var clickEntry: ITouchEntry? = null
    private var clickTransfer: ITouchTransfer? = null


    class CounterViewHolder(private var binding: RecyclerCounterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SimpleDateFormat")
        fun bind(
            counter: Counter ,
            context: Context ,
            clickEdit: ITouchEdit? ,
            clickDelete: ITouchDelete? ,
            clickSales: ITouchSales? ,
            clickAmount: ITouchAmount? ,
            clickLocation: ITouchLocation? ,
            clickEntry: ITouchEntry? ,
            clickTransfer: ITouchTransfer?
        ) {

            //Declare
            val amount = counter.amount
            val size = counter.size
            val location = counter.location
            val salePrice= counter.salePrice




            binding.tvSize.text = size
            binding.tvAmount.text = amount.toString()
            binding.tvLocation.text = location
            binding.tvSalePrice.text=salePrice.toString()
            binding.menuOption.setOnClickListener {
                val popupMenu = PopupMenu(context , binding.menuOption)
                popupMenu.menuInflater.inflate(R.menu.menu_option_reten , popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_option_reten_edit -> {
                            clickEdit?.onClickEdit(counter)

                        }

                        R.id.menu_option_reten_delete -> {
                            clickDelete?.onClickDelete(counter)
                        }

                        R.id.menu_option_reten_sales -> {
                            clickSales?.onClickSales(counter)
                        }

                        R.id.menu_option_reten_amount -> {
                            clickAmount?.onClickAmount(counter)
                        }

                        R.id.menu_option_reten_location -> {
                            clickLocation?.onClickLocation(counter)
                        }

                        R.id.menu_option_reten_entry -> {
                            clickEntry?.onClickEntry(counter)
                        }

                        R.id.menu_option_reten_transfer -> {
                            clickTransfer?.onClickTransfer(counter)
                        }
                    }
                    false
                }
                popupMenu.show()

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): CounterViewHolder {
        val viewHolder = CounterViewHolder(
            RecyclerCounterBinding.inflate(
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


    override fun onBindViewHolder(holder: CounterViewHolder , position: Int) {
        holder.bind(
            getItem(position) ,
            context ,
            clickEdit ,
            clickDelete ,
            clickSales ,
            clickAmount ,
            clickLocation ,
            clickEntry ,
            clickTransfer
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

    //Details
    interface ITouchEdit {
        fun onClickEdit(counter: Counter)
    }

    fun setClickEdit(clickEdit: ITouchEdit?) {
        this.clickEdit = clickEdit
    }

    interface ITouchDelete {
        fun onClickDelete(counter: Counter)
    }

    fun setClickDelete(clickDelete: ITouchDelete?) {
        this.clickDelete = clickDelete
    }

    interface ITouchSales {
        fun onClickSales(counter: Counter)
    }

    fun setClickSales(clickSales: ITouchSales?) {
        this.clickSales = clickSales
    }

    interface ITouchAmount {
        fun onClickAmount(counter: Counter)
    }

    fun setClickAmount(clickAmount: ITouchAmount?) {
        this.clickAmount = clickAmount
    }

    interface ITouchLocation {
        fun onClickLocation(counter: Counter)
    }

    fun setClickLocation(clickLocation: ITouchLocation?) {
        this.clickLocation = clickLocation
    }

    interface ITouchEntry {
        fun onClickEntry(counter: Counter)
    }

    fun setClickEntry(clickEntry: ITouchEntry?) {
        this.clickEntry = clickEntry
    }

    interface ITouchTransfer {
        fun onClickTransfer(counter: Counter)
    }

    fun setClickTransfer(clickTransfer: ITouchTransfer?) {
        this.clickTransfer = clickTransfer
    }


}