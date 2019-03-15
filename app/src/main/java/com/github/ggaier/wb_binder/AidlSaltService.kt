package com.github.ggaier.wb_binder

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

/**
 * Created by wenbo, 2019/3/15
 */
class AidlSaltService : Service() {

    private val binder = object : ISaltGeneratorInterface.Stub() {
        override fun generateSalt(prefix: String?, listener: ISaltListener?) {
            val result = prefix + generateRandomMessage()
            Log.d(TAG, "AidlSaltService thread: ${Thread.currentThread()}")
            Thread.sleep(2000)
            listener?.onSaltGened(result)
        }

    }

    override fun onBind(intent: Intent?): IBinder? = binder

}