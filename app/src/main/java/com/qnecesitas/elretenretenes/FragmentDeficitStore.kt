package com.qnecesitas.elretenretenes

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.qnecesitas.elretenretenes.adapters.AdapterDeficitStore
import com.qnecesitas.elretenretenes.data.Store
import com.qnecesitas.elretenretenes.databinding.FragmentDeficitStoreBinding
import com.qnecesitas.elretenretenes.databinding.LiAddEntryBinding
import com.qnecesitas.elretenretenes.viewmodels.Repository
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FragmentDeficitStore : Fragment() {

    private lateinit var binding: FragmentDeficitStoreBinding

    private var li_amount_binding: LiAddEntryBinding? = null

    //Recycler
    private lateinit var alStore: MutableList<Store>
    private lateinit var adapterStore: AdapterDeficitStore

    private lateinit var repository: Repository


    override fun onCreateView(
        inflater: LayoutInflater ,
        container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDeficitStoreBinding.inflate(inflater , container , false)

        repository = Repository(activity?.application as ElRetenRetenes)

        //Recycler
        binding.recycler.setHasFixedSize(true)
        alStore = mutableListOf()
        adapterStore = AdapterDeficitStore()
        binding.recycler.adapter = adapterStore

        //Observe
        repository.listStore.observe(requireActivity()) {
            adapterStore.submitList(it)
        }
        repository.listStoreFilter.observe(requireActivity()) {
            adapterStore.submitList(it)
        }


        adapterStore.setClickEntry(object : AdapterDeficitStore.ITouchEntry {
            override fun onClickEntry(store: Store) {
                entryAmount(store)
            }

        })
        loadRecyclerInfoAll()
        return binding.root
    }

    fun entryAmount(store: Store) {
        val inflater = LayoutInflater.from(binding.root.context)
        li_amount_binding = LiAddEntryBinding.inflate(inflater)
        val builder = AlertDialog.Builder(binding.root.context)
        builder.setView(li_amount_binding!!.root)
        val alertDialog = builder.create()

        //Filling and listeners
        loadRecyclerInfoAll()
        var currentAmount = 0
        li_amount_binding!!.et.setText(currentAmount.toString())

        li_amount_binding!!.ivBtnMore.setOnClickListener {
            if (currentAmount != 99999) {
                currentAmount++
                li_amount_binding!!.et.setText(currentAmount.toString())
            }
        }

        li_amount_binding!!.ivBtnLess.setOnClickListener {
            if (currentAmount != 0) {
                currentAmount--
                li_amount_binding!!.et.setText(currentAmount.toString())
            }
        }

        li_amount_binding!!.et.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(charSequence: CharSequence , i: Int , i1: Int , i2: Int) {
                if (li_amount_binding!!.et.text.toString() == "0") {
                    currentAmount = 1
                    li_amount_binding!!.et.setText(currentAmount.toString())
                } else if (li_amount_binding!!.et.text.toString() == "") {
                    currentAmount = 1
                } else {
                    currentAmount = li_amount_binding!!.et.text.toString().toInt()
                }
            }

            override fun afterTextChanged(editable: Editable) {}
            override fun beforeTextChanged(
                charSequence: CharSequence ,
                i: Int ,
                i1: Int ,
                i2: Int
            ) {
            }
        })

        li_amount_binding!!.btnAccept.setOnClickListener {
            alertDialog.dismiss()
            if (li_amount_binding!!.et.text.toString().isNotBlank()) {
                lifecycleScope.launch {
                    repository.updateAmountStore(
                        store.code ,
                        li_amount_binding!!.et.text.toString().toInt() + store.amount
                    )
                    FancyToast.makeText(
                        requireContext() ,
                        getString(R.string.operacion_realizada_con_exito) ,
                        FancyToast.LENGTH_LONG ,
                        FancyToast.SUCCESS ,
                        false
                    ).show()
                }

            } else {
                li_amount_binding!!.et.error = getString(R.string.este_campo_no_debe_vacio)
            }
        }

        li_amount_binding!!.btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        //Finished
        alertDialog.setCancelable(false)
        alertDialog.window!!.setGravity(Gravity.CENTER)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun loadRecyclerInfoAll() {
        GlobalScope.launch(Dispatchers.IO) {
            repository.getAllRetenStore()

        }
    }

    fun callFilterByText(text: String) {
        repository.filterByTextStore(text)
    }


}