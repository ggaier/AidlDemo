package com.github.ggaier.wb_binder

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

fun randomMessage(): String{
    val SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
    val salt = StringBuilder()
    val rnd = Random()
    while (salt.length < 18) { // length of the random string.
        val index = (rnd.nextFloat() * SALTCHARS.length).toInt()
        salt.append(SALTCHARS[index])
    }
    return salt.toString()
}
const val MSG_SHOW_RANDOM_SALT = 1
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        localBinder.setOnClickListener {
            generateRandomMsgLocally()
        }
        messengerBound.setOnClickListener {
            generateMsgViaMessenger()
        }

    }

    private val clientMessenger: Messenger = Messenger(object: Handler(){
        override fun handleMessage(msg: Message?) {
            when(msg?.what){
                MSG_SHOW_RANDOM_SALT ->
                    Log.d(TAG, "${msg.obj}")
            }
        }
    })

    private var messengerService: Messenger? = null
    private var messengerServiceConn = object: ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {
            messengerService = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            messengerService = Messenger(service)
        }
    }
    private fun generateMsgViaMessenger() {
        if(messengerService==null){
            Intent(this, MessengerService::class.java).also {
                bindService(it, messengerServiceConn, Context.BIND_AUTO_CREATE)
            }
        }else{
            messengerService?.send(Message.obtain(null, MSG_RANDOM_SALT, 0, 0).also {
                it.replyTo = clientMessenger
            })
        }
    }

    private var localBinderService: LocalBinderService? = null
    private val localServiceConnection = object: ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "localService disconnected: $name")
            localBinderService = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "localService connected: $name, $service")
            localBinderService = (service as? LocalBinderService.LocalBinder)?.getService()
        }
    }
    private fun generateRandomMsgLocally() {
        if (localBinderService == null) {
            Intent(this, LocalBinderService::class.java).also {
                bindService(it, localServiceConnection, Context.BIND_AUTO_CREATE)
            }
        } else {
            Log.d(TAG, "${localBinderService?.randomMessage()}")
        }
    }
}
