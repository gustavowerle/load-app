package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    private lateinit var selectedItem: String
    private lateinit var status: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        cancelNotification()
        getExtraInformation()
        fillInformation()

        ok_button.setOnClickListener {
            finish()
        }
    }

    private fun cancelNotification() {
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.cancelAll()
    }

    private fun fillInformation() {
        text_item_selected.text = selectedItem
        text_status.text = status
    }

    private fun getExtraInformation() {
        selectedItem = intent.getStringExtra(SELECTED_ITEM_EXTRA).toString()
        status = intent.getStringExtra(STATUS_EXTRA).toString()
    }

    companion object {
        const val SELECTED_ITEM_EXTRA = "selected_item"
        const val STATUS_EXTRA = "status"
    }

}
