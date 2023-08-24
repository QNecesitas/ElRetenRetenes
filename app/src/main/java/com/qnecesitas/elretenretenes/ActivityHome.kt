package com.qnecesitas.elretenretenes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.FragmentManager
import com.google.android.material.tabs.TabLayout
import com.qnecesitas.elretenretenes.databinding.ActivityHomeBinding


class ActivityHome : AppCompatActivity() {

    //Binding
    private lateinit var binding: ActivityHomeBinding



    //TabLayout
    private lateinit var tabLayout: TabLayout



    private lateinit var fragmentCounter: FragmentCounter
    private lateinit var fragmentStore: FragmentStore
    private lateinit var fragmentStatistics: FragmentStatistics

    private var tabSelect = 0


    //Fragments
    private lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }



        //Fragments
        fragmentManager = supportFragmentManager
        fragmentCounter = FragmentCounter()
        fragmentStore = FragmentStore()
        fragmentStatistics=FragmentStatistics()

        //Settings
        binding.ivIconSetting.setOnClickListener {
            val intent=Intent(this,ActivitySettings::class.java)
            startActivity(intent)
        }

        //SearchView
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (tabSelect == 0) {
                    fragmentCounter.callFilterByText(newText.toString())
                } else if (tabSelect == 1) {
                    fragmentStore.callFilterByText(newText.toString())
                }
                return false
            }

        })
        binding.ivIconSearch.setOnClickListener {
            binding.clSearch.visibility = View.VISIBLE
            binding.ivIconSearch.visibility = View.GONE
            binding.ivIconSetting.visibility = View.GONE
            binding.search.setQuery("" , false)
        }
        binding.ivCloseSearch.setOnClickListener {
            binding.clSearch.visibility = View.GONE
            binding.ivIconSearch.visibility = View.VISIBLE
            binding.ivIconSetting.visibility = View.VISIBLE
            binding.search.setQuery("" , false)
        }


        //TabLayout
        tabLayout = binding.tabLayout
        tabLayout.addTab(tabLayout.newTab().setText("Mostrador"))
        tabLayout.addTab(tabLayout.newTab().setText("Almacén"))
        tabLayout.addTab(tabLayout.newTab().setText("Estadísticas"))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        // Código para ejecutar cuando se selecciona la pestaña Mostrador
                        showFragmentCounter()
                        tabSelect = 0
                        binding.clSearch.visibility = View.GONE
                        binding.ivIconSearch.visibility = View.VISIBLE
                        binding.ivIconSetting.visibility = View.VISIBLE
                    }

                    1 -> {
                        // Código para ejecutar cuando se selecciona la pestaña Almacén
                        showFragmentStore()
                        tabSelect = 1
                        binding.clSearch.visibility = View.GONE
                        binding.ivIconSearch.visibility = View.VISIBLE
                        binding.ivIconSetting.visibility = View.VISIBLE
                    }

                    2 ->{
                        //Código para ejecutar cuando se selecciona la pestaña Estadísticas
                        showFragmentStatistics()
                        tabSelect = 2
                        binding.clSearch.visibility = View.GONE
                        binding.ivIconSearch.visibility = View.GONE
                        binding.ivIconSetting.visibility = View.VISIBLE
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                // Código para ejecutar cuando una pestaña deja de estar seleccionada
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // Código para ejecutar cuando una pestaña ya seleccionada se vuelve a seleccionar
            }
        })


        showFragmentCounter()
    }

    fun showFragmentCounter() {
        fragmentManager.beginTransaction()
            .replace(R.id.frame , fragmentCounter)
            .commit()
    }

    fun showFragmentStore() {
        fragmentManager.beginTransaction()
            .replace(R.id.frame , fragmentStore)
            .commit()
    }

    fun showFragmentStatistics() {
        fragmentManager.beginTransaction()
            .replace(R.id.frame , fragmentStatistics)
            .commit()
    }





}