package br.com.alex.taxi.api.error

import br.com.alex.taxi.model.Ride
import br.com.alex.taxi.model.User
import retrofit2.Response
import retrofit2.adapter.rxjava.HttpException
import rx.Observable

/**
 * Created by alex on 13/06/16.
 */
object RideExceptionMapper {

    fun fromException(httpException: Throwable?): Observable<out Ride>? {
        return if (httpException is HttpException) {

            when (httpException.code()) {
                410 -> {
                    Observable.error(RideException(httpException.message()))
                }
                404 -> {
                    Observable.error(RideNotFoundException())
                }
                else -> {
                    Observable.error(httpException)
                }
            }

        } else {
            Observable.error<Ride>(httpException)
        }
    }

    fun fromResponse(response: Response<Void>): Response<Void> {

        when (response.code()) {
            410 -> {
                throw RideException(response.errorBody().string())
            }
            404 -> {
                throw RideNotFoundException()
            }
        }

        return response
    }
}