package com.github.ggaier.wb_binder

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

fun generateRandomMessage(): String {
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

        aidlBound.setOnClickListener {
            generateSaltViaAidl()
        }

        aidlBound2.setOnClickListener {
            generateSaltViaAidlSync()
        }

    }

    private fun generateSaltViaAidlSync() {
        if (saltService == null) {
            bindServiceUseAldl()
        } else {
            try {
                Log.d(TAG, "AidlSaltService say: "+saltService?.syncGenerateSalt("")+", ${Thread.currentThread()}")
            } catch (e: DeadObjectException) {
                e.printStackTrace()
            } catch (se: SecurityException){
                se.printStackTrace()
            }
        }
    }

    private val saltServiceListener: ISaltListener = object: ISaltListener.Stub(){
        override fun onSaltGened(salt: String?) {
            Log.d(TAG, "ISaltListener onSaltGened: $salt, on Thread: ${Thread.currentThread()}")
        }
    }
    private var saltService: ISaltGeneratorInterface? = null
    private fun generateSaltViaAidl() {
        if (saltService == null) {
            bindServiceUseAldl()
        } else {
            try {
                saltService?.generateSalt("AidlSaltService say: ", saltServiceListener)
            } catch (e: DeadObjectException) {
                e.printStackTrace()
            } catch (se: SecurityException){
                se.printStackTrace()
            }
        }
    }

    private fun bindServiceUseAldl() {
        Intent(this, AidlSaltService::class.java).also {
            bindService(it, object : ServiceConnection {
                override fun onServiceDisconnected(name: ComponentName?) {
                    Log.d(TAG, "AidlSaltService onServiceDisconnected: $name")
                    saltService = null
                }

                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    Log.d(
                        TAG, "AidlSaltService binder descriptor: ${service?.interfaceDescriptor}, $service, " +
                                "$name"
                    )
                    saltService = ISaltGeneratorInterface.Stub.asInterface(service)
                    Log.d(TAG, "salt service: $saltService")
                }

            }, Context.BIND_AUTO_CREATE)
        }
    }

    private val clientMessenger: Messenger = Messenger(@SuppressLint("HandlerLeak") object : Handler() {
        override fun handleMessage(msg: Message?) {
            when (msg?.what) {
                MSG_SHOW_RANDOM_SALT ->
                    Log.d(TAG, "${(msg.obj as Bundle)["data"]}")
            }
        }
    })

    private var messengerService: Messenger? = null
    private var messengerServiceConn = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            messengerService = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "messengerService onServiceConnected: $name, $service")
            messengerService = Messenger(service)
        }
    }

    private fun generateMsgViaMessenger() {
        if (messengerService == null) {
            Intent(this, MessengerService::class.java).also {
                bindService(it, messengerServiceConn, Context.BIND_AUTO_CREATE)
            }
        } else {
            try {
                messengerService?.send(Message.obtain(null, MSG_RANDOM_SALT, 0, 0).also {
                    it.replyTo = clientMessenger
                })
            } catch (e: DeadObjectException) {
            }
        }
    }

    private var localBinderService: LocalBinderService? = null
    private val localServiceConnection = object : ServiceConnection {
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
            try {
                Log.d(TAG, "${localBinderService?.randomMessage()}")
            } catch (e: DeadObjectException) {
            }
        }
    }
}
