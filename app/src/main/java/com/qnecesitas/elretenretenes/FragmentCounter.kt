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
import com.qnecesitas.elretenretenes.databinding.FragmentCounterBinding
import com.qnecesitas.elretenretenes.viewmodels.CounterViewModel
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.qnecesitas.elretenretenes.adapters.CounterAdapter
import com.qnecesitas.elretenretenes.auxiliary.IDCreater
import com.qnecesitas.elretenretenes.data.Counter
import com.qnecesitas.elretenretenes.databinding.LiAddRetenBinding
import com.qnecesitas.elretenretenes.databinding.LiEditAmountBinding
import com.qnecesitas.elretenretenes.databinding.LiEditRetenBinding
import com.qnecesitas.elretenretenes.viewmodels.CounterViewModelFactory
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FragmentCounter : Fragment() {

    //Binding
    private lateinit var binding: FragmentCounterBinding

    //Add Session
    private var li_add_binding: LiAddRetenBinding? = null
    private var li_edit_binding:LiEditRetenBinding? = null
    private var li_amount_binding:LiEditAmountBinding?= null

    //Recycler
    private lateinit var alCounter : MutableList<Counter>
    private lateinit var adapterCounter:CounterAdapter

    //View Model
    private val viewModel: CounterViewModel by viewModels{
        CounterViewModelFactory((activity?.application as ElRetenRetenes).database.counterDao())
    }
    var section="Mostrador"

    override fun onCreateView(
        inflater: LayoutInflater ,
        container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCounterBinding.inflate(inflater, container, false)

        //Recycler
        binding.recycler.setHasFixedSize(true)
        alCounter = mutableListOf()
        adapterCounter = CounterAdapter(requireContext())
        binding.recycler.adapter = adapterCounter

        binding.btnAcept.setOnClickListener {
            liAddReten()
        }



        //Observe
        viewModel.listCounter.observe(requireActivity()) {
            adapterCounter.submitList(it)
        }
        viewModel.listCounterFilter.observe(requireActivity()) {
            adapterCounter.submitList(it)
        }

        adapterCounter.setClickEdit(object :CounterAdapter.ITouchEdit{
            override fun onClickEdit(counter: Counter) {
                liEditReten(counter)
            }

        })

        adapterCounter.setClickAmount(object :CounterAdapter.ITouchAmount{
            override fun onClickAmount(counter: Counter) {
                liEditAmount(counter)
            }

        })

        adapterCounter.setClickLocation(object :CounterAdapter.ITouchLocation{
            override fun onClickLocation(counter: Counter) {
                getLocation(counter)
            }

        })
        adapterCounter.setClickDelete(object :CounterAdapter.ITouchDelete{
            override fun onClickDelete(counter: Counter) {
                deleteReten(counter)
            }

        })

        adapterCounter.setClickEntry(object :CounterAdapter.ITouchEntry{
            override fun onClickEntry(counter: Counter) {
                entryAmount(counter)
            }

        })
        loadRecyclerInfoAll()

        return binding.root
    }

    private fun liEditReten(counter: Counter){
        val inflater = LayoutInflater.from(binding.root.context)
        li_edit_binding = LiEditRetenBinding.inflate(inflater)
        val builder = AlertDialog.Builder(binding.root.context)
        builder.setView(li_edit_binding?.root)
        val alertDialog = builder.create()
        loadRecyclerInfoAll()

        //Variables
        var code:String = counter.code
        var location:String=counter.location
        var amount:Int= counter.amount.toString().toInt()
        var buyPrice:Double = counter.buyPrice.toString().toDouble()
        var salePrice:Double= counter.salePrice.toString().toDouble()
        var descr:String= counter.descr
        var deficit:Int = counter.deficit.toString().toInt()
        var size:String= counter.size
        var brand:String = counter.brand

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
                location= li_edit_binding?.tietLocalizacion?.text.toString()
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
                            Counter(
                                code,brand,  amount, buyPrice, salePrice, descr, deficit, size, location
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
        if(li_edit_binding?.tietBrand?.text?.trim()!!.isEmpty()){
            li_edit_binding?.tietBrand?.setText(getString(R.string.no_definido))
        }

        //Size
        if(li_edit_binding?.tietSize?.text?.trim()!!.isEmpty()){
            li_edit_binding?.tietSize?.setText(getString(R.string.no_definido))
        }
        if(li_edit_binding?.tietDesc?.text?.trim()!!.isEmpty()){
            li_edit_binding?.tietDesc?.setText(getString(R.string.no_definido))
        }


        return amountTrue == 1
    }

    private fun editProductDB(counter:Counter) {
        lifecycleScope.launch {
            viewModel.updateReten(
                counter.code,
                counter.location,
                counter.amount,
                counter.buyPrice,
                counter.salePrice,
                counter.descr,
                counter.deficit,
                counter.size,
                counter.brand
            )
        }

        FancyToast.makeText(
            requireContext(),
            getString(R.string.operacion_realizada_con_exito),
            FancyToast.LENGTH_LONG,
            FancyToast.SUCCESS,
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
                location= li_add_binding?.tietLocalizacion?.text.toString()
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
                            Counter(
                                code,brand,  amount, buyPrice, salePrice, descr, deficit, size, location
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
        if(li_add_binding?.tietBrand?.text?.trim()!!.isEmpty()){
            li_add_binding?.tietBrand?.setText(getString(R.string.no_definido))
        }

        //Size
        if(li_add_binding?.tietSize?.text?.trim()!!.isEmpty()){
            li_add_binding?.tietSize?.setText(getString(R.string.no_definido))
        }
        if(li_add_binding?.tietDesc?.text?.trim()!!.isEmpty()){
            li_add_binding?.tietDesc?.setText(getString(R.string.no_definido))
        }


        return amountTrue == 1
    }

    private fun addProductDB(counter:Counter) {
        lifecycleScope.launch {
            viewModel.addReten(
                counter.code,
                counter.location,
                counter.amount,
                counter.buyPrice,
                counter.salePrice,
                counter.descr,
                counter.deficit,
                counter.size,
                counter.brand
            )
        }

        FancyToast.makeText(
            requireContext(),
            getString(R.string.operacion_realizada_con_exito),
            FancyToast.LENGTH_LONG,
            FancyToast.SUCCESS,
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
        builder.setPositiveButton(R.string.aceptar) { dialog, _ ->
            dialog.dismiss()
        }

        //create the alert dialog and show it
        builder.create().show()
    }

    //LoadRecycler
    @OptIn(DelicateCoroutinesApi::class)
    fun loadRecyclerInfoAll(){
        GlobalScope.launch(Dispatchers.IO) {
            viewModel.getAllReten()
        }
    }

    fun liEditAmount(counter: Counter){
        val inflater = LayoutInflater.from(binding.root.context)
        li_amount_binding = LiEditAmountBinding.inflate(inflater)
        val builder = AlertDialog.Builder(binding.root.context)
        builder.setView(li_amount_binding!!.root)
        val alertDialog = builder.create()

        //Filling and listeners
        loadRecyclerInfoAll()
        var currentAmount: Int = counter.amount
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
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
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
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }
        })

        li_amount_binding!!.btnAccept.setOnClickListener {
            alertDialog.dismiss()
            if (li_amount_binding!!.et.text.toString().isNotBlank()) {
                lifecycleScope.launch{
                    viewModel.updateAmount(counter.code, li_amount_binding!!.et.text.toString().toInt())
                    FancyToast.makeText(
                        requireContext(),
                        getString(R.string.operacion_realizada_con_exito),
                        FancyToast.LENGTH_LONG,
                        FancyToast.SUCCESS,
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

    fun getLocation(counter: Counter){
        loadRecyclerInfoAll()

        FancyToast.makeText(
            requireContext(),
            ("Localización: ${counter.location}.\nSección: $section."),
            FancyToast.LENGTH_LONG,
            FancyToast.SUCCESS,
            false
        ).show()

    }
    fun callFilterByText(text: String) {
        viewModel.filterByText(text)
    }
    @OptIn(DelicateCoroutinesApi::class)
    fun deleteReten(counter: Counter){
        GlobalScope.launch(Dispatchers.IO){
            viewModel.deleteReten(counter.code)
        }
        FancyToast.makeText(
            requireContext(),
            getString(R.string.operacion_realizada_con_exito),
            FancyToast.LENGTH_LONG,
            FancyToast.SUCCESS,
            false
        ).show()
    }
    fun entryAmount(counter: Counter){
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
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
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
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }
        })

        li_amount_binding!!.btnAccept.setOnClickListener {
            alertDialog.dismiss()
            if (li_amount_binding!!.et.text.toString().isNotBlank()) {
                lifecycleScope.launch{
                    viewModel.updateAmount(counter.code, li_amount_binding!!.et.text.toString().toInt() + counter.amount)
                    FancyToast.makeText(
                        requireContext(),
                        getString(R.string.operacion_realizada_con_exito),
                        FancyToast.LENGTH_LONG,
                        FancyToast.SUCCESS,
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
}
