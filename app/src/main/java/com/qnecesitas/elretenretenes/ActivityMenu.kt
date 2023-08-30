package com.qnecesitas.elretenretenes

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import com.qnecesitas.elretenretenes.auxiliary.Constants
import com.qnecesitas.elretenretenes.databinding.ActivityMenuBinding
import com.qnecesitas.elretenretenes.viewmodels.Repository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ActivityMenu : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private lateinit var repository: Repository

    //Notification
    private val CHANNEL_ID: String = "ElReten"
    private val CHANNEL_NAME = "ElReten"
    lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = Repository(application as ElRetenRetenes)


        //Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        //Notification
        val context = applicationContext
        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        binding.cvStore.setOnClickListener {
            val intent = Intent(this , ActivityHome::class.java)
            startActivity(intent)
        }
        binding.ivIconSetting.setOnClickListener {
            val intent = Intent(this , ActivitySettings::class.java)
            startActivity(intent)
        }
        binding.cvProduct.setOnClickListener {
            val intent = Intent(this , ActivityStatistics::class.java)
            startActivity(intent)
        }
        binding.cvCounter.setOnClickListener {
            val intent = Intent(this , ActivityDeficit::class.java)
            startActivity(intent)
        }

        if (!Constants.Notificado) {
            Constants.Notificado = true
            ifNotification()
        }
    }

    private fun displayNotification1(counter: Int): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(CHANNEL_ID , CHANNEL_NAME , NotificationManager.IMPORTANCE_HIGH)
            channel.description = getString(R.string.d_ficit)
            channel.enableLights(true)
            channel.lightColor = Color.RED
            notificationManager.createNotificationChannel(channel)
        }
        val context = applicationContext
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(context , CHANNEL_ID)
                .setContentTitle(getString(R.string.d_ficit))
                .setContentText("Mostrador: $counter productos.")
                .setSmallIcon(R.drawable.ic_launcher_round)
                .setAutoCancel(false)

        return builder.build()

    }

    private fun displayNotification2(store: Int): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(CHANNEL_ID , CHANNEL_NAME , NotificationManager.IMPORTANCE_HIGH)
            channel.description = getString(R.string.d_ficit)
            channel.enableLights(true)
            channel.lightColor = Color.RED
            notificationManager.createNotificationChannel(channel)
        }
        val context = applicationContext
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(context , CHANNEL_ID)
                .setContentTitle(getString(R.string.d_ficit))
                .setContentText("Almacén: $store productos.")
                .setSmallIcon(R.drawable.ic_launcher_round)
                .setAutoCancel(false)

        return builder.build()

    }

    @OptIn(DelicateCoroutinesApi::class)
    fun ifNotification() {
        lifecycleScope.launch {
            val deficitCounter = repository.getDeficitCounter()
            val deficitStore = repository.getDeficitStore()
            if (deficitCounter >= 1 && deficitStore >= 1) {
                notificationManager.notify(8 , displayNotification1(deficitCounter))
                GlobalScope.launch {
                    delay(1000)
                    notificationManager.notify(9 , displayNotification2(deficitStore))
                }
            } else if (deficitCounter >= 1 && deficitStore < 1) {
                notificationManager.notify(9 , displayNotification1(deficitCounter))
            } else if (deficitStore >= 1 && deficitCounter < 1) {
                notificationManager.notify(9 , displayNotification2(deficitStore))
            }
        }
    }
}