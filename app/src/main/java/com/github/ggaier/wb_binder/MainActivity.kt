package com.github.ggaier.wb_binder

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        localBinder.setOnClickListener {
            bindingServiceLocally()
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
    private fun bindingServiceLocally() {
        Intent(this, LocalBinderService::class.java).also {
            bindService(it, localServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }
}
