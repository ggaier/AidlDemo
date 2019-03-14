package com.github.ggaier.wb_binder

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

/**
 * Created by wenbo, 2019/3/13
 */
const val TAG = "ggaier_binder"

class LocalBinderService : Service() {

    private val binder = LocalBinder()

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * 随机的发送一个消息
     */
    fun randomMessage(): String {
        return "LocalBinder says: ${randomMessage()}"
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "BinderService onDestroy")
    }

    inner class LocalBinder() : Binder() {
        fun getService() = this@LocalBinderService
    }

}