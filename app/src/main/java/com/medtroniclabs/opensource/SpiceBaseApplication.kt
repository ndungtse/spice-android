package com.medtroniclabs.opensource

import android.app.Application
import com.medtroniclabs.microcoaching.Language
import com.medtroniclabs.microcoaching.MicroCoachingSDK
import com.medtroniclabs.opensource.BuildConfig
import com.medtroniclabs.opensource.appextensions.isDebug
import com.medtroniclabs.opensource.custom.SecuredPreference
import com.medtroniclabs.opensource.log.CrashReportingTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class SpiceBaseApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        initTimber()
        initPreference()
        initCoachingSdk()
    }

    private fun initCoachingSdk() {
        MicroCoachingSDK.Builder(this)
            .language(Language.BANGLA)
            .backendUrl(BuildConfig.COACHING_BACKEND_URL)
            .authToken("") // token not yet available; LandingActivity reinitialises with JWT
            .enableTelemetry(BuildConfig.ENABLE_COACHING_TELEMETRY)
            .enableChat(true)
            .build()
    }

    /**
     * method to print debug and release logs
     */
    private fun initTimber() {
        isDebug { debug ->
            if (debug)
                Timber.plant(Timber.DebugTree())
            else
                Timber.plant(CrashReportingTree())
        }
    }

    /**
     * method to initialize preference
     */
    private fun initPreference() {
        SecuredPreference
            .Builder()
            .build(packageName, applicationContext)
    }
}