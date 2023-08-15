package com.qnecesitas.elretenretenes
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
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

    //Recycler
    private lateinit var alCounter : MutableList<Counter>
    private lateinit var adapterCounter:CounterAdapter

    //View Model
    private val viewModel: CounterViewModel by viewModels{
        CounterViewModelFactory((activity?.application as ElRetenRetenes).database.counterDao())
    }

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

        loadRecyclerInfoAll()

        Log.e("XXX","${viewModel.listCounter.value?.size}")

        viewModel.listCounter.observe(requireActivity()) {
            adapterCounter.submitList(it)
        }

        return binding.root
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
        if (li_add_binding?.tietLocalizacion?.text!!.trim().isNotEmpty()) {
            amountTrue++
        } else {
            li_add_binding?.tilLocalizacion?.error = getString(R.string.este_campo_no_debe_vacio)
        }

        //Code
        if (li_add_binding?.tietCode?.text!!.trim().isNotEmpty()) {
            amountTrue++
        } else {
            li_add_binding?.tilCode?.error = getString(R.string.este_campo_no_debe_vacio)
        }

        //PriceBuy
        if (li_add_binding?.tietPriceBuy?.text!!.trim().isNotEmpty()) {
            amountTrue++
        } else {
            li_add_binding?.tilPriceBuy?.error = getString(R.string.este_campo_no_debe_vacio)
        }

        //PriceSale
        if (li_add_binding?.tietPriceSale?.text!!.trim().isNotEmpty()) {
            amountTrue++
        } else {
            li_add_binding?.tilPriceSale?.error = getString(R.string.este_campo_no_debe_vacio)
        }

        //Amount
        if (li_add_binding?.tietCant?.text?.trim()!!.isNotEmpty()) {
            amountTrue++
        } else {
            li_add_binding?.tilCant?.error = getString(R.string.este_campo_no_debe_vacio)
        }

        //Deficit
        if (li_add_binding?.tietDeficit?.text?.trim()!!.isNotEmpty()) {
            amountTrue++
        } else {
            li_add_binding?.tilDeficit?.error = getString(R.string.este_campo_no_debe_vacio)
        }

        return amountTrue == 6
    }

    private fun addProductDB(counter:Counter) {
        lifecycleScope.launch {
            viewModel.addProduct(
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
}
