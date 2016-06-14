package br.com.alex.taxi.androidapp

import android.os.StrictMode
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import com.squareup.leakcanary.LeakCanary

/**
 * Created by alex on 09/06/16.
 */
class AlexTaxiApplication: MultiDexApplication() {

    override fun onCreate() {

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }

        super.onCreate()
        MultiDex.install(this)
        LeakCanary.install(this)
    }
}