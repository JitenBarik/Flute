package com.flute.music

import android.app.Application
import com.flute.music.di.AppContainer

class FluteApp : Application() {

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        appContainer = AppContainer(this)
    }

    companion object {
        private lateinit var instance: FluteApp

        val container: AppContainer
            get() = instance.appContainer
    }
}
