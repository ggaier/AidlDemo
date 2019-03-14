package com.github.ggaier.wb_binder

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.util.Log

/**
 * Created by wenbo, 2019/3/14
 */
const val MSG_RANDOM_SALT = 1

class MessengerService() : Service() {

    private lateinit var messager: Messenger

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "MessengerService: onBind")
        messager = Messenger(IncomingHandler(this))
        return messager.binder
    }

    private class IncomingHandler(context: Context, private val appContext: Context = context.applicationContext) :
        Handler() {

        override fun handleMessage(msg: Message?) {
            if (msg == null) return
            when (msg.what) {
                MSG_RANDOM_SALT ->
                    msg.replyTo?.send(Message.obtain(null, MSG_SHOW_RANDOM_SALT, randomMessage()))
                else -> super.handleMessage(msg)
            }
        }


    }

}