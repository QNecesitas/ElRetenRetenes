package com.qnecesitas.elretenretenes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.qnecesitas.elretenretenes.databinding.ActivityStatisticsBinding

class ActivityStatistics : AppCompatActivity() {

    private lateinit var binding: ActivityStatisticsBinding

    private lateinit var fragment: FragmentStatistics

    private lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        //Fragments
        fragmentManager = supportFragmentManager
        fragment = FragmentStatistics()

        //Settings
        binding.ivIconSetting.setOnClickListener {
            val intent = Intent(this , ActivitySettings::class.java)
            startActivity(intent)
        }
        showFragmentStatistics()
    }

    fun showFragmentStatistics() {
        fragmentManager.beginTransaction()
            .replace(R.id.frame , fragment)
            .commit()
    }
}