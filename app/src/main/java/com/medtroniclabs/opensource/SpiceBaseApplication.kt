package com.medtroniclabs.opensource

import android.app.Application
import com.medtroniclabs.microcoaching.Language
import com.medtroniclabs.microcoaching.MicroCoachingSDK
import com.medtroniclabs.microcoaching.ModelDownloadStrategy
import com.medtroniclabs.microcoaching.ai.model.ModelProvider
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
        val modelDir = getExternalFilesDir(null)
        val existingModel = modelDir?.listFiles()
            ?.firstOrNull { it.extension == "task" || it.extension == "litertlm" }
        val downloadStrategy = if (existingModel != null) ModelDownloadStrategy.PROVIDED
                               else ModelDownloadStrategy.ON_FIRST_USE
        MicroCoachingSDK.Builder(this)
            .language(Language.BANGLA)
            .backendUrl(BuildConfig.COACHING_BACKEND_URL)
            .authToken("")
            .enableTelemetry(BuildConfig.ENABLE_COACHING_TELEMETRY)
            .enableChat(true)
            .modelDownloadStrategy(downloadStrategy)
            .modelProviders(listOf(ModelProvider.HuggingFace))
            .modelPath(existingModel?.absolutePath ?: "")
            .huggingFaceToken(BuildConfig.HF_TOKEN)
            .wifiOnlyModelDownload(false)
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