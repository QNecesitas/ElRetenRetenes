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
import com.qnecesitas.elretenretenes.adapters.AdapterDeficit
import com.qnecesitas.elretenretenes.data.Counter
import com.qnecesitas.elretenretenes.databinding.FragmentDeficitCounterBinding
import com.qnecesitas.elretenretenes.databinding.LiAddEntryBinding
import com.qnecesitas.elretenretenes.viewmodels.Repository
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FragmentDeficitCounter : Fragment() {

    private lateinit var binding: FragmentDeficitCounterBinding

    private var li_amount_binding: LiAddEntryBinding? = null

    //Recycler
    private lateinit var alCounter: MutableList<Counter>
    private lateinit var adapterCounter: AdapterDeficit

    private lateinit var repository: Repository


    override fun onCreateView(
        inflater: LayoutInflater ,
        container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDeficitCounterBinding.inflate(inflater , container , false)

        repository = Repository(activity?.application as ElRetenRetenes)

        //Recycler
        binding.recycler.setHasFixedSize(true)
        alCounter = mutableListOf()
        adapterCounter = AdapterDeficit()
        binding.recycler.adapter = adapterCounter

        //Observe
        repository.listCounter.observe(requireActivity()) {
            adapterCounter.submitList(it)
        }
        repository.listCounterFilter.observe(requireActivity()) {
            adapterCounter.submitList(it)
        }


        adapterCounter.setClickEntry(object : AdapterDeficit.ITouchEntry {
            override fun onClickEntry(counter: Counter) {
                entryAmount(counter)
            }

        })
        loadRecyclerInfoAll()
        return binding.root
    }

    fun entryAmount(counter: Counter) {
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
                    repository.updateAmount(
                        counter.code ,
                        li_amount_binding!!.et.text.toString().toInt() + counter.amount
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
            repository.getAllRetenCounter()

        }
    }

    fun callFilterByText(text: String) {
        repository.filterByTextCounter(text)
    }


}