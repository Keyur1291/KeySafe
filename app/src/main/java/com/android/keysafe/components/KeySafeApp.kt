package com.android.keysafe.components

import android.app.Application
import com.android.keysafe.model.Graph

class KeySafeApp: Application() {

    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }

}