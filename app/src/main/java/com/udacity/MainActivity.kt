package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

private const val SUCCESS = "Success"
private const val FAILED = "Failed"

class MainActivity : AppCompatActivity() {

    private lateinit var notificationManager: NotificationManager
    private lateinit var selectedItem: String
    private var downloadID: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        prepareNotificationStuff()
        setCustomButtonBehavior()
    }

    private fun setCustomButtonBehavior() {
        custom_button.setOnClickListener {
            if (radio_group.checkedRadioButtonId == -1) {
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.select_one_option),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            custom_button.setNewButtonState(ButtonState.Loading)
            download()
        }
    }

    private fun prepareNotificationStuff() {
        notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(applicationContext)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    private fun download() {
        val request = DownloadManager.Request(Uri.parse(getUrl()))
            .setTitle(getString(R.string.app_name))
            .setDescription(getString(R.string.app_description))
            .setRequiresCharging(false)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        // enqueue puts the download request in the queue.
        downloadID = downloadManager.enqueue(request)
    }

    private fun getUrl() = when (radio_group.checkedRadioButtonId) {
        radio_button_glide.id -> {
            selectedItem = getString(R.string.glide_description)
            getString(R.string.glide_url)
        }
        radio_button_load_app.id -> {
            selectedItem = getString(R.string.load_app_description)
            getString(R.string.load_app_url)
        }
        radio_button_retrofit.id -> {
            selectedItem = getString(R.string.retrofit_description)
            getString(R.string.retrofit_url)
        }
        else -> ""
    }

    private fun createNotification(status: Int) {
        when (status) {
            DownloadManager.STATUS_SUCCESSFUL -> {
                notificationManager.createNotification(
                    applicationContext,
                    selectedItem,
                    SUCCESS
                )
            }
            else -> {
                notificationManager.createNotification(
                    applicationContext,
                    selectedItem,
                    FAILED
                )
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id && intent.action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                radio_group.clearCheck()
                custom_button.setNewButtonState(ButtonState.Completed)
                val query = DownloadManager.Query()
                query.setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0))
                val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val cursor = manager.query(query)
                if (cursor.moveToFirst() && cursor.count > 0) {
                    createNotification(cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)))
                }
            }
        }
    }
}
