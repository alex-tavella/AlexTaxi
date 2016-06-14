package br.com.alex.taxi.api

import br.com.alex.taxi.model.Ride
import br.com.alex.taxi.model.User
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import rx.Observable

/**
 * Created by alex on 12/06/16.
 */
interface TaxisService {

    companion object Client {
        internal val BASE_URL = "http://ec2-54-88-12-34.compute-1.amazonaws.com/"
    }

    object Factory {
        fun createDefault(): TaxisService {
            return Retrofit.Builder()
                    .client(OkHttpClientFactory.singletonInstance)
                    .baseUrl(TaxisService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build().create(TaxisService::class.java)
        }
    }

    @FormUrlEncoded
    @POST("v1/users")
    fun registerUser(@Field("name") name: String): Observable<Response<Void>>

    @FormUrlEncoded
    @POST("v1/users/{userId}")
    fun editUser(@Path("userId") userId: Long, @Field("name") name: String): Observable<User>

    @GET("v1/users/{userId}")
    fun getUser(@Path("userId") userId: Long): Observable<User>

    @GET
    fun getUser(@Url userUrl: String): Observable<User>

    @PUT("v1/ride")
    fun requestRide(): Observable<Response<Void>>

    @GET("v1/ride/{rideId}")
    fun getRide(@Path("rideId") rideId: Long): Observable<Ride>

    @GET
    fun getRide(@Url rideUrl: String): Observable<Ride>

}