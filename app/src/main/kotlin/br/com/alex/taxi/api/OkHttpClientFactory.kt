package br.com.alex.taxi.api

import br.com.alex.taxi.androidapp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Created by alex on 03/06/16.
 */
internal class OkHttpClientFactory {
    companion object {
        val singletonInstance: OkHttpClient by lazy { val okHttpClientBuilder = OkHttpClient.Builder()

            if (BuildConfig.DEBUG) {
                okHttpClientBuilder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
            }

            okHttpClientBuilder.build()
        }
    }
}