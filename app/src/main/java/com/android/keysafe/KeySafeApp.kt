package com.android.keysafe

import android.app.Application

class KeySafeApp: Application() {

    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }

}