package br.com.alex.taxi.api

import br.com.alex.taxi.model.Taxi
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

/**
 * Created by alex on 12/06/16.
 */
interface LastLocationsService {

    companion object Client {
        internal val BASE_URL = "https://api.99taxis.com/"
    }

    object Factory {
        fun createDefault(): LastLocationsService {
            return Retrofit.Builder()
                    .client(OkHttpClientFactory.singletonInstance)
                    .baseUrl(LastLocationsService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build().create(LastLocationsService::class.java)
        }
    }

    @GET("lastLocations")
    fun getTaxisInRegion(@Query("sw") swCoords: String, @Query("ne") neCoords: String): Observable<List<Taxi>>
}