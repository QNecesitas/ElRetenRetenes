package com.qnecesitas.elretenretenes

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.qnecesitas.elretenretenes.adapters.AdapterStore
import com.qnecesitas.elretenretenes.auxiliary.IDCreater
import com.qnecesitas.elretenretenes.data.Store
import com.qnecesitas.elretenretenes.databinding.FragmentStoreBinding
import com.qnecesitas.elretenretenes.databinding.LiAddRetenBinding
import com.qnecesitas.elretenretenes.databinding.LiEditAmountBinding
import com.qnecesitas.elretenretenes.databinding.LiEditRetenBinding
import com.qnecesitas.elretenretenes.databinding.LiTransferAmountBinding
import com.qnecesitas.elretenretenes.viewmodels.StoreViewModel
import com.qnecesitas.elretenretenes.viewmodels.StoreViewModelFactory
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FragmentStore : Fragment() {

    //Binding
    private lateinit var binding: FragmentStoreBinding

    //Add Session
    private var li_add_binding: LiAddRetenBinding? = null
    private var li_edit_binding: LiEditRetenBinding? = null
    private var li_amount_binding: LiEditAmountBinding? = null
    private var li_transfer_binding: LiTransferAmountBinding? = null

    //Recycler
    private lateinit var alStore: MutableList<Store>
    private lateinit var adapterStore: AdapterStore

    //View Model
    private val viewModel: StoreViewModel by viewModels {
        StoreViewModelFactory((activity?.application as ElRetenRetenes).database.storeDao())
    }
    var section = "Almacén"

    override fun onCreateView(
        inflater: LayoutInflater ,
        container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStoreBinding.inflate(inflater , container , false)

        //Recycler
        binding.recycler.setHasFixedSize(true)
        alStore = mutableListOf()
        adapterStore = AdapterStore(requireContext())
        binding.recycler.adapter = adapterStore

        binding.btnAcept.setOnClickListener {
            liAddReten()
        }




        viewModel.listStore.observe(requireActivity()) {
            adapterStore.submitList(it)
        }


        viewModel.listStoreFilter.observe(requireActivity()) {
            adapterStore.submitList(it)
        }

        adapterStore.setClickEdit(object : AdapterStore.ITouchEdit {
            override fun onClickEdit(store: Store) {
                liEditReten(store)
            }

        })

        adapterStore.setClickAmount(object : AdapterStore.ITouchAmount {
            override fun onClickAmount(store: Store) {
                liEditAmount(store)
            }

        })

        adapterStore.setClickLocation(object : AdapterStore.ITouchLocation {
            override fun onClickLocation(store: Store) {
                getLocation(store)
            }

        })

        adapterStore.setClickDelete(object : AdapterStore.ITouchDelete {
            override fun onClickDelete(store: Store) {
                deleteReten(store)
            }

        })

        adapterStore.setClickEntry(object : AdapterStore.ITouchEntry {
            override fun onClickEntry(store: Store) {
                entryAmount(store)
            }

        })

        adapterStore.setClickTransfer(object : AdapterStore.ITouchTransfer {
            override fun onClickTransfer(store: Store) {
                liTransferAmount(store)
            }

        })
        loadRecyclerInfoAll()

        return binding.root
    }

    private fun liEditReten(store: Store) {
        val inflater = LayoutInflater.from(binding.root.context)
        li_edit_binding = LiEditRetenBinding.inflate(inflater)
        val builder = AlertDialog.Builder(binding.root.context)
        builder.setView(li_edit_binding?.root)
        val alertDialog = builder.create()
        loadRecyclerInfoAll()

        //Variables
        var code: String = store.code
        var location: String = store.location
        var amount: Int = store.amount.toString().toInt()
        var buyPrice: Double = store.buyPrice.toString().toDouble()
        var salePrice: Double = store.salePrice.toString().toDouble()
        var descr: String = store.descr
        var deficit: Int = store.deficit.toString().toInt()
        var size: String = store.size
        var brand: String = store.brand

        li_edit_binding?.tietCode?.setText(code)
        li_edit_binding?.tietLocalizacion?.setText(location)
        li_edit_binding?.tietCant?.setText(amount.toString())
        li_edit_binding?.tietPriceBuy?.setText(buyPrice.toString())
        li_edit_binding?.tietPriceSale?.setText(salePrice.toString())
        li_edit_binding?.tietDesc?.setText(descr)
        li_edit_binding?.tietDeficit?.setText(deficit.toString())
        li_edit_binding?.tietSize?.setText(size)
        li_edit_binding?.tietBrand?.setText(brand)
        li_edit_binding?.btnCancel?.setOnClickListener { alertDialog.dismiss() }
        li_edit_binding?.btnAccept?.setOnClickListener {
            if (checkInfoDataEdit()) {
                //Instances
                code = li_edit_binding?.tietCode?.text.toString()
                location = li_edit_binding?.tietLocalizacion?.text.toString()
                amount = li_edit_binding?.tietCant?.text.toString().toInt()
                buyPrice = li_edit_binding?.tietPriceBuy?.text.toString().toDouble()
                salePrice = li_edit_binding?.tietPriceSale?.text.toString().toDouble()
                descr = li_edit_binding?.tietDesc?.text.toString()
                deficit = li_edit_binding?.tietDeficit?.text.toString().toInt()
                size = li_edit_binding?.tietSize?.text.toString()
                brand = li_edit_binding?.tietBrand?.text.toString()
                alertDialog.dismiss()

                lifecycleScope.launch {
                    editProductDB(
                        Store(
                            code ,
                            brand ,
                            amount ,
                            buyPrice ,
                            salePrice ,
                            descr ,
                            deficit ,
                            size ,
                            location
                        )
                    )
                }
            }

        }


        //Finished
        alertDialog.setCancelable(false)
        alertDialog.window!!.setGravity(Gravity.CENTER)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

    private fun checkInfoDataEdit(): Boolean {
        var amountTrue = 0

        //Locate
        if (li_edit_binding?.tietLocalizacion?.text!!.trim().isEmpty()) {
            li_edit_binding?.tietLocalizacion?.setText(getString(R.string.no_definido))
        }

        //Code
        if (li_edit_binding?.tietCode?.text!!.trim().isNotEmpty()) {
            amountTrue++
        } else {
            li_edit_binding?.tilCode?.error = getString(R.string.este_campo_no_debe_vacio)
        }

        //PriceBuy
        if (li_edit_binding?.tietPriceBuy?.text!!.trim().isEmpty()) {
            li_edit_binding?.tietPriceBuy?.setText(0)
        }

        //PriceSale
        if (li_edit_binding?.tietPriceSale?.text!!.trim().isEmpty()) {
            li_edit_binding?.tietPriceSale?.setText(0)
        }

        //Amount
        if (li_edit_binding?.tietCant?.text?.trim()!!.isEmpty()) {
            li_edit_binding?.tietCant?.setText(0)
        }

        //Deficit
        if (li_edit_binding?.tietDeficit?.text?.trim()!!.isEmpty()) {
            li_edit_binding?.tietDeficit?.setText(0)
        }
        //Brand
        if (li_edit_binding?.tietBrand?.text?.trim()!!.isEmpty()) {
            li_edit_binding?.tietBrand?.setText(getString(R.string.no_definido))
        }

        //Size
        if (li_edit_binding?.tietSize?.text?.trim()!!.isEmpty()) {
            li_edit_binding?.tietSize?.setText(getString(R.string.no_definido))
        }
        if (li_edit_binding?.tietDesc?.text?.trim()!!.isEmpty()) {
            li_edit_binding?.tietDesc?.setText(getString(R.string.no_definido))
        }


        return amountTrue == 1
    }

    private fun editProductDB(store: Store) {
        lifecycleScope.launch {
            viewModel.updateReten(
                store.code ,
                store.location ,
                store.amount ,
                store.buyPrice ,
                store.salePrice ,
                store.descr ,
                store.deficit ,
                store.size ,
                store.brand
            )
        }

        FancyToast.makeText(
            requireContext() ,
            getString(R.string.operacion_realizada_con_exito) ,
            FancyToast.LENGTH_LONG ,
            FancyToast.SUCCESS ,
            false
        ).show()


    }


    private fun liAddReten() {
        val inflater = LayoutInflater.from(binding.root.context)
        li_add_binding = LiAddRetenBinding.inflate(inflater)
        val builder = AlertDialog.Builder(binding.root.context)
        builder.setView(li_add_binding?.root)
        val alertDialog = builder.create()

        //Variables
        var code = IDCreater.generate()
        var location: String
        var amount: Int
        var buyPrice: Double
        var salePrice: Double
        var descr: String
        var deficit: Int
        var size: String
        var brand: String

        li_add_binding?.tietCode?.setText(code)
        li_add_binding?.btnCancel?.setOnClickListener { alertDialog.dismiss() }
        li_add_binding?.btnAccept?.setOnClickListener {
            if (checkInfoDataAdd()) {
                //Instances
                code = li_add_binding?.tietCode?.text.toString()
                location = li_add_binding?.tietLocalizacion?.text.toString()
                amount = li_add_binding?.tietCant?.text.toString().toInt()
                buyPrice = li_add_binding?.tietPriceBuy?.text.toString().toDouble()
                salePrice = li_add_binding?.tietPriceSale?.text.toString().toDouble()
                descr = li_add_binding?.tietDesc?.text.toString()
                deficit = li_add_binding?.tietDeficit?.text.toString().toInt()
                size = li_add_binding?.tietSize?.text.toString()
                brand = li_add_binding?.tietBrand?.text.toString()
                alertDialog.dismiss()

                lifecycleScope.launch {
                    if (!viewModel.selectDuplicate(code)) {
                        addProductDB(
                            Store(
                                code ,
                                brand ,
                                amount ,
                                buyPrice ,
                                salePrice ,
                                descr ,
                                deficit ,
                                size ,
                                location
                            )
                        )
                    } else {
                        showAlertDialogDuplicated()
                    }
                }

            }
        }

        //Finished
        alertDialog.setCancelable(false)
        alertDialog.window!!.setGravity(Gravity.CENTER)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }


    private fun checkInfoDataAdd(): Boolean {
        var amountTrue = 0

        //Locate
        if (li_add_binding?.tietLocalizacion?.text!!.trim().isEmpty()) {
            li_add_binding?.tietLocalizacion?.setText(getString(R.string.no_definido))
        }

        //Code
        if (li_add_binding?.tietCode?.text!!.trim().isNotEmpty()) {
            amountTrue++
        } else {
            li_add_binding?.tilCode?.error = getString(R.string.este_campo_no_debe_vacio)
        }

        //PriceBuy
        if (li_add_binding?.tietPriceBuy?.text!!.trim().isEmpty()) {
            li_add_binding?.tietPriceBuy?.setText(0)
        }

        //PriceSale
        if (li_add_binding?.tietPriceSale?.text!!.trim().isEmpty()) {
            li_add_binding?.tietPriceSale?.setText(0)
        }

        //Amount
        if (li_add_binding?.tietCant?.text?.trim()!!.isEmpty()) {
            li_add_binding?.tietCant?.setText(0)
        }

        //Deficit
        if (li_add_binding?.tietDeficit?.text?.trim()!!.isEmpty()) {
            li_add_binding?.tietDeficit?.setText(0)
        }
        //Brand
        if (li_add_binding?.tietBrand?.text?.trim()!!.isEmpty()) {
            li_add_binding?.tietBrand?.setText(getString(R.string.no_definido))
        }

        //Size
        if (li_add_binding?.tietSize?.text?.trim()!!.isEmpty()) {
            li_add_binding?.tietSize?.setText(getString(R.string.no_definido))
        }
        if (li_add_binding?.tietDesc?.text?.trim()!!.isEmpty()) {
            li_add_binding?.tietDesc?.setText(getString(R.string.no_definido))
        }


        return amountTrue == 1
    }

    private fun addProductDB(store: Store) {
        lifecycleScope.launch {
            viewModel.addReten(
                store.code ,
                store.location ,
                store.amount ,
                store.buyPrice ,
                store.salePrice ,
                store.descr ,
                store.deficit ,
                store.size ,
                store.brand
            )
        }

        FancyToast.makeText(
            requireContext() ,
            getString(R.string.operacion_realizada_con_exito) ,
            FancyToast.LENGTH_LONG ,
            FancyToast.SUCCESS ,
            false
        ).show()


    }

    private fun showAlertDialogDuplicated() {
        //init alert dialog
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setCancelable(true)
        builder.setTitle(R.string.elemento_repetido)
        builder.setMessage(R.string.elemento_repetido_desc)
        //set listeners for dialog buttons
        builder.setPositiveButton(R.string.aceptar) { dialog , _ ->
            dialog.dismiss()
        }

        //create the alert dialog and show it
        builder.create().show()
    }

    //LoadRecycler
    @OptIn(DelicateCoroutinesApi::class)
    fun loadRecyclerInfoAll() {
        GlobalScope.launch(Dispatchers.IO) {
            viewModel.getAllReten()
        }
    }

    fun liEditAmount(store: Store) {
        val inflater = LayoutInflater.from(binding.root.context)
        li_amount_binding = LiEditAmountBinding.inflate(inflater)
        val builder = AlertDialog.Builder(binding.root.context)
        builder.setView(li_amount_binding!!.root)
        val alertDialog = builder.create()

        //Filling and listeners
        loadRecyclerInfoAll()
        var currentAmount: Int = store.amount
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
                    viewModel.updateAmount(
                        store.code ,
                        li_amount_binding!!.et.text.toString().toInt()
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

    fun getLocation(store: Store) {
        loadRecyclerInfoAll()

        FancyToast.makeText(
            requireContext() ,
            ("Localización: ${store.location}.\nSección: $section.") ,
            FancyToast.LENGTH_LONG ,
            FancyToast.SUCCESS ,
            false
        ).show()

    }

    fun callFilterByText(text: String) {
        viewModel.filterByText(text)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun deleteReten(store: Store) {
        GlobalScope.launch(Dispatchers.IO) {
            viewModel.deleteReten(store.code)
        }
        FancyToast.makeText(
            requireContext() ,
            getString(R.string.operacion_realizada_con_exito) ,
            FancyToast.LENGTH_LONG ,
            FancyToast.SUCCESS ,
            false
        ).show()
    }

    fun entryAmount(store: Store) {
        val inflater = LayoutInflater.from(binding.root.context)
        li_amount_binding = LiEditAmountBinding.inflate(inflater)
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
                    viewModel.updateAmount(
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

    fun liTransferAmount(store: Store) {
        val inflater = LayoutInflater.from(binding.root.context)
        li_transfer_binding = LiTransferAmountBinding.inflate(inflater)
        val builder = AlertDialog.Builder(binding.root.context)
        builder.setView(li_transfer_binding!!.root)
        val alertDialog = builder.create()

        //Filling and listeners
        loadRecyclerInfoAll()
        var currentAmount = 0
        li_transfer_binding!!.et.setText(currentAmount.toString())

        li_transfer_binding!!.ivBtnMore.setOnClickListener {
            if (currentAmount != store.amount) {
                currentAmount++
                li_transfer_binding!!.et.setText(currentAmount.toString())
            }
        }

        li_transfer_binding!!.ivBtnLess.setOnClickListener {
            if (currentAmount != 0) {
                currentAmount--
                li_transfer_binding!!.et.setText(currentAmount.toString())
            }
        }

        li_transfer_binding!!.et.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(charSequence: CharSequence , i: Int , i1: Int , i2: Int) {
                if (li_transfer_binding!!.et.text.toString() == "0") {
                    currentAmount = 1
                    li_transfer_binding!!.et.setText(currentAmount.toString())
                } else if (li_transfer_binding!!.et.text.toString() == "") {
                    currentAmount = 1
                } else {
                    currentAmount = li_transfer_binding!!.et.text.toString().toInt()
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

        li_transfer_binding!!.btnAccept.setOnClickListener {
            lifecycleScope.launch {
                if (li_transfer_binding!!.et.text.toString()
                        .isNotBlank() || li_transfer_binding!!.et.text.toString()
                        .toInt() <= store.amount
                ) {
                    val count = viewModel.isDuplicateCounter(store.code)
                    if (count == store.code) {
                        val amount = viewModel.getAmountCounter(store.code)
                        viewModel.updateAmountCounter(
                            count ,
                            amount + li_transfer_binding!!.et.text.toString().toInt()
                        )
                        viewModel.updateAmount(
                            store.code ,
                            store.amount - li_transfer_binding!!.et.text.toString().toInt()
                        )
                    } else {
                        viewModel.updateAmount(
                            store.code ,
                            store.amount - li_transfer_binding!!.et.text.toString().toInt()
                        )
                        viewModel.addCounter(
                            store.code ,
                            store.location ,
                            li_transfer_binding!!.et.text.toString().toInt() ,
                            store.buyPrice ,
                            store.salePrice ,
                            store.descr ,
                            store.deficit ,
                            store.size ,
                            store.brand
                        )
                    }


                    alertDialog.dismiss()



                    FancyToast.makeText(
                        requireContext() ,
                        getString(R.string.operacion_realizada_con_exito) ,
                        FancyToast.LENGTH_LONG ,
                        FancyToast.SUCCESS ,
                        false
                    ).show()


                } else {
                    li_transfer_binding!!.et.error =
                        getString(R.string.no_puede_transferir_esa_cantidad)
                }
            }
        }

        li_transfer_binding!!.btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        //Finished
        alertDialog.setCancelable(false)
        alertDialog.window!!.setGravity(Gravity.CENTER)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

    }
}
