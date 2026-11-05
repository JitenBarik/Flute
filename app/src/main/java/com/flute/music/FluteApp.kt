package com.flute.music

import android.app.Application
import android.util.Log
import com.flute.music.di.AppContainer

class FluteApp : Application() {

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("FluteFatal", "Crash on ${thread.name}: ${throwable.message}", throwable)
            try {
                val prefs = getSharedPreferences("flute_crash_log", MODE_PRIVATE)
                prefs.edit().putString("last_crash", Log.getStackTraceString(throwable)).commit()
            } catch (e: Exception) {
                // ignore
            }
            throwable.printStackTrace()
        }

        try {
            appContainer = AppContainer(this)
        } catch (e: Throwable) {
            Log.e("FluteApp", "Failed to create AppContainer", e)
        }
    }

    companion object {
        var instance: FluteApp? = null
            private set

        val container: AppContainer
            get() = instance?.appContainer ?: throw IllegalStateException("FluteApp not initialized")
    }
}
