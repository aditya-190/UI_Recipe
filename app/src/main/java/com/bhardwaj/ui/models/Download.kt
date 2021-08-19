package com.bhardwaj.ui.models

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.bhardwaj.ui.R

class Download(
    private val url: String,
    private val fileName: String,
    private val mContext: Context
) {
    private var downloadReference: Long = 0
    private lateinit var downloadManager: DownloadManager

    fun downloadFile() {
        try {
            val downloadRequest = DownloadManager.Request(Uri.parse(url))
            downloadManager = mContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadRequest.apply {
                setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                setTitle(fileName)
                setDescription(mContext.getString(R.string.download_progress))
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            }
            mContext.registerReceiver(
                receiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            )
            downloadReference = downloadManager.enqueue(downloadRequest)
        } catch (e: Exception) {
            println(e)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(mContext: Context, intent: Intent) {
            val action = intent.action
            if (action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (downloadId != downloadReference) {
                    mContext.unregisterReceiver(this)
                    return
                }
                val query = DownloadManager.Query()
                query.setFilterById(downloadReference)
                val cursor = downloadManager.query(query)
                cursor?.let {
                    if (cursor.moveToFirst()) {
                        val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(columnIndex)) {
                            Toast.makeText(
                                mContext,
                                mContext.getString(R.string.download_success),
                                Toast.LENGTH_SHORT
                            ).show()

                        } else if (DownloadManager.STATUS_FAILED == cursor.getInt(columnIndex)) {
                            Toast.makeText(
                                mContext,
                                mContext.getString(R.string.download_failed),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    cursor.close()
                }
                mContext.unregisterReceiver(this)
            }
        }
    }
}