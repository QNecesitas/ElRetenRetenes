package com.qnecesitas.elretenretenes

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.qnecesitas.elretenretenes.data.Sales
import com.qnecesitas.elretenretenes.databinding.FragmentStatisticsBinding
import com.qnecesitas.elretenretenes.databinding.LiDateYBinding
import com.qnecesitas.elretenretenes.databinding.LiDateYmBinding
import com.qnecesitas.elretenretenes.databinding.LiDateYmdBinding
import com.qnecesitas.elretenretenes.viewmodels.Repository
import kotlinx.coroutines.launch
import java.util.Calendar


class FragmentStatistics : Fragment() {

    private lateinit var binding: FragmentStatisticsBinding
    private lateinit var repository: Repository
    private lateinit var alSalesAll: MutableList<Sales>
    private lateinit var listFilter: MutableList<Sales>

    //Ranking
    private lateinit var alMonthSalesRanking: MutableList<Sales>


    override fun onCreateView(
        inflater: LayoutInflater ,
        container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatisticsBinding.inflate(inflater , container , false)

        repository = Repository(activity?.application as ElRetenRetenes)
        alSalesAll = ArrayList()
        alMonthSalesRanking = ArrayList()
        listFilter = ArrayList()

        //Date
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY

        //profits Day
        binding.ivProfitDayCalendar.setOnClickListener {
            liDateDay(this::callProfitDay)
        }
        binding.tvProfitDayResponse.setOnClickListener {
            liDateDay(this::callProfitDay)
        }

        //profits Month
        binding.ivProfitMonthCalendar.setOnClickListener {
            liDateMonth(this::callProfitMonth)
        }
        binding.tvProfitMonthResponse.setOnClickListener {
            liDateMonth(this::callProfitMonth)
        }

        //profits Year
        binding.ivProfitYearCalendar.setOnClickListener {
            liDateYear(this::callProfitYear)
        }
        binding.tvProfitYearResponse.setOnClickListener {
            liDateYear(this::callProfitYear)
        }

        //sales Day
        binding.ivSalesDayCalendar.setOnClickListener {
            liDateDay(this::callSalesDay)
        }
        binding.tvSalesDayResponse.setOnClickListener {
            liDateDay(this::callSalesDay)
        }

        //sales Month
        binding.ivSalesMonthCalendar.setOnClickListener {
            liDateMonth(this::callSalesMonth)
        }
        binding.tvSalesMonthResponse.setOnClickListener {
            liDateMonth(this::callSalesMonth)
        }

        //sales Year
        binding.ivSalesYearCalendar.setOnClickListener {
            liDateYear(this::callSalesYear)
        }
        binding.tvSalesYearResponse.setOnClickListener {
            liDateYear(this::callSalesYear)
        }



        loadSalesAllMonths()
        return binding.root
    }

    private fun loadSalesAllMonths() {
        lifecycleScope.launch {
            val date = Calendar.getInstance()
            val month = date.get(Calendar.MONTH) + 1
            val monthLast = month - 1
            val year = date.get(Calendar.YEAR)
            val day = date.get(Calendar.DAY_OF_MONTH)
            alSalesAll = repository.fetchSalesStatistics(month , monthLast , year)
            Log.e("XXX" , "${alSalesAll.size}")
            val listMonth = alSalesAll.filter {
                it.month == month
            }
            Log.e("YYY" , "${listMonth.size}")
            val cantMonth = 30 - day
            Log.e("BBB" , "$cantMonth")
            date.set(year , monthLast - 1 , 1)
            val lastDay = date.getActualMaximum(Calendar.DAY_OF_MONTH)
            Log.e("AAA" , "$lastDay")
            val dayLastDate = lastDay - cantMonth
            val listMonthLast = alSalesAll.filter {
                it.day > dayLastDate
            }
            Log.e("ZZZ" , "${listMonthLast.size}")
            val listFilter = listMonth + listMonthLast
            Log.e("WWW" , "${listFilter.size}")

            updateChart(listFilter)
        }

    }

    private fun updateChart(list: List<Sales>) {
        prepareDataChart(list)
    }

    private fun prepareDataChart(list: List<Sales>) {
        val listOfEntries = mutableListOf<Entry>()

        for (x in lastDay().indices) {
            val salesByMonth = ArrayList<Sales>()
            for (model in list) {
                if (model.day == lastDay()[x].toInt()) {
                    salesByMonth.add(model)
                }
            }
            listOfEntries.add(Entry(x.toFloat() , composeProfit(salesByMonth).toFloat()))
        }


        drawChart(listOfEntries)
    }


    //Profits Day
    private fun callProfitDay(year: Int , month: Int , day: Int) {
        lifecycleScope.launch {
            val alFetched = repository.fetchOrdersD(year , month , day)

            binding.tvProfitDayResponse.text = composeProfit(alFetched)
            binding.tvProfitDayResponse.visibility = View.VISIBLE
        }

    }


    //Profits Month
    private fun callProfitMonth(year: Int , month: Int) {
        lifecycleScope.launch {
            val alFetched = repository.fetchOrdersM(year , month)

            binding.tvProfitMonthResponse.text = composeProfit(alFetched)
            binding.tvProfitMonthResponse.visibility = View.VISIBLE
        }
    }


    //Profits Year
    private fun callProfitYear(year: Int) {
        lifecycleScope.launch {
            val alFetched = repository.fetchOrdersY(year)
            binding.tvProfitYearResponse.text = composeProfit(alFetched)
            binding.tvProfitYearResponse.visibility = View.VISIBLE
        }
    }


    fun composeProfit(list: MutableList<Sales>): String {
        var totalPrice = 0.0
        var totalInv = 0.0
        list.forEach {
            totalPrice += it.totalPrice
            totalInv += it.totalInv
        }
        return (totalPrice - totalInv).toString()
    }

    //Sales Day
    private fun callSalesDay(year: Int , month: Int , day: Int) {
        lifecycleScope.launch {
            val alFetched = repository.fetchOrdersD(year , month , day)
            binding.tvSalesDayResponse.text = alFetched.size.toString()
            binding.tvSalesDayResponse.visibility = View.VISIBLE
        }
    }


    //Sales Month
    private fun callSalesMonth(year: Int , month: Int) {
        lifecycleScope.launch {
            val alFetched = repository.fetchOrdersM(year , month)

            binding.tvSalesMonthResponse.text = alFetched.size.toString()
            binding.tvSalesMonthResponse.visibility = View.VISIBLE
        }

    }


    //Sales Year
    private fun callSalesYear(year: Int) {

        lifecycleScope.launch {
            val response = repository.fetchOrdersY(year)
            binding.tvSalesYearResponse.text = response.size.toString()
            binding.tvSalesYearResponse.visibility = View.VISIBLE
        }
    }


    private fun liDateYear(actionSet: (Int) -> Unit) {
        val inflater = LayoutInflater.from(binding.root.context)
        val liBinding = LiDateYBinding.inflate(inflater)
        val builder = AlertDialog.Builder(binding.root.context)
        builder.setView(liBinding.root)
        val alertDialog = builder.create()

        //Declare
        val calendar = Calendar.getInstance()
        liBinding.ilNpAnno.maxValue = 2050
        liBinding.ilNpAnno.minValue = 2020
        liBinding.ilNpAnno.value = calendar.get(Calendar.YEAR)

        //Listeners
        liBinding.btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }
        liBinding.btnAcept.setOnClickListener {
            alertDialog.dismiss()
            val year = liBinding.ilNpAnno.value
            actionSet(year)
        }

        //Finish
        alertDialog.setCancelable(false)
        alertDialog.window!!.setGravity(Gravity.CENTER)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

    }

    private fun liDateMonth(actionSet: (Int , Int) -> Unit) {

        val inflater = LayoutInflater.from(binding.root.context)
        val liBinding = LiDateYmBinding.inflate(inflater)
        val builder = AlertDialog.Builder(binding.root.context)
        builder.setView(liBinding.root)
        val alertDialog = builder.create()

        //Declare
        val calendar = Calendar.getInstance()
        liBinding.ilNpAnnos.maxValue = 2050
        liBinding.ilNpAnnos.minValue = 2020
        liBinding.ilNpAnnos.value = calendar.get(Calendar.YEAR)
        liBinding.ilNpMonth.maxValue = 11
        liBinding.ilNpMonth.minValue = 0
        liBinding.ilNpMonth.value = calendar.get(Calendar.MONTH)
        val months = arrayOf(
            "Enero" ,
            "Febrero" ,
            "Marzo" ,
            "Abril" ,
            "Mayo" ,
            "Junio" ,
            "Julio" ,
            "Agosto" ,
            "Septiembre" ,
            "Octubre" ,
            "Noviembre" ,
            "Diciembre"
        )
        liBinding.ilNpMonth.displayedValues = months

        //Listeners
        liBinding.btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }
        liBinding.btnAcept.setOnClickListener {
            alertDialog.dismiss()
            val year = liBinding.ilNpAnnos.value
            val month = liBinding.ilNpMonth.value + 1
            actionSet(year , month)
        }

        //Finish
        alertDialog.setCancelable(false)
        alertDialog.window!!.setGravity(Gravity.CENTER)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

    }

    private fun liDateDay(actionSet: (Int , Int , Int) -> Unit) {

        val inflater = LayoutInflater.from(binding.root.context)
        val liBinding = LiDateYmdBinding.inflate(inflater)
        val builder = AlertDialog.Builder(binding.root.context)
        builder.setView(liBinding.root)
        val alertDialog = builder.create()

        //Declare
        val calendar = Calendar.getInstance()
        liBinding.ilNpAnnos.maxValue = 2050
        liBinding.ilNpAnnos.minValue = 2020
        liBinding.ilNpAnnos.value = calendar.get(Calendar.YEAR)
        liBinding.ilNpMonth.maxValue = 11
        liBinding.ilNpMonth.minValue = 0
        liBinding.ilNpMonth.value = calendar.get(Calendar.MONTH)
        liBinding.ilNpDay.minValue = 1
        liBinding.ilNpDay.maxValue = 31
        liBinding.ilNpDay.value = calendar.get(Calendar.DAY_OF_MONTH)
        val months = arrayOf(
            "Enero" ,
            "Febrero" ,
            "Marzo" ,
            "Abril" ,
            "Mayo" ,
            "Junio" ,
            "Julio" ,
            "Agosto" ,
            "Septiembre" ,
            "Octubre" ,
            "Noviembre" ,
            "Diciembre"
        )
        liBinding.ilNpMonth.displayedValues = months

        //Listeners
        liBinding.btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }
        liBinding.btnAcept.setOnClickListener {
            alertDialog.dismiss()
            val year = liBinding.ilNpAnnos.value
            val month = liBinding.ilNpMonth.value + 1
            val day = liBinding.ilNpDay.value
            actionSet(year , month , day)
        }

        //Finish
        alertDialog.setCancelable(false)
        alertDialog.window!!.setGravity(Gravity.CENTER)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

    //update chart
    private fun drawChart(entries: List<Entry>) {

        //create LineDataSet
        val dataSet = LineDataSet(entries , "Dias")
        dataSet.color = R.color.orange
        dataSet.valueTextColor = Color.RED
        dataSet.axisDependency = YAxis.AxisDependency.LEFT //axisY only in left side
        //create LineData will show the chart
        val lineData = LineData(dataSet)
        binding.chart.data = lineData
        //create tags for axisX trying to don't show intermediate values
        val labels = lastDay()
        //set the formatter with the tags
        val formatter = IndexAxisValueFormatter(labels)
        binding.chart.xAxis.valueFormatter = formatter
        //minor setting
        binding.chart.description.text = "Ganancias en los ultimos 30 días" //description
        binding.chart.setTouchEnabled(true) //user can't interact with chart
        binding.chart.animateY(1000) //inner animation
        binding.chart.xAxis.position = XAxis.XAxisPosition.BOTTOM //axisX in bottom
    }

    private fun lastDay(): List<String> {
        val calendar = Calendar.getInstance()
        val dates = mutableListOf<String>()
        calendar.add(Calendar.DAY_OF_MONTH , 0)
        dates.add(0 , calendar.get(Calendar.DAY_OF_MONTH).toString())

        for (i in 0 until 29) {
            calendar.add(Calendar.DAY_OF_MONTH , -1)
            dates.add(0 , calendar.get(Calendar.DAY_OF_MONTH).toString())
        }

        return dates
    }

}