package br.com.alex.taxi.androidapp

import android.os.Bundle
import android.support.multidex.MultiDex
import android.support.test.runner.AndroidJUnitRunner

/**
 * Created by alex on 14/06/16.
 */
class AppCustomRunner: AndroidJUnitRunner() {

    override fun onCreate(arguments: Bundle?) {
        MultiDex.install(targetContext)
        super.onCreate(arguments)
    }
}