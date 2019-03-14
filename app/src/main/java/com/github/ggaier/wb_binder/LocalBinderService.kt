package com.github.ggaier.wb_binder

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import java.util.*

/**
 * Created by wenbo, 2019/3/13
 */
const val TAG = "ggaier_binder"
class LocalBinderService: Service(){

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
    fun randomMessage() : String {
        val SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
        val salt = StringBuilder().append("LocalBinder says: ")
        val rnd = Random()
        while (salt.length < 18) { // length of the random string.
            val index = (rnd.nextFloat() * SALTCHARS.length).toInt()
            salt.append(SALTCHARS[index])
        }
        return salt.toString()
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "BinderService onDestroy")
    }

    inner class LocalBinder(): Binder(){
        fun getService() = this@LocalBinderService
    }

}