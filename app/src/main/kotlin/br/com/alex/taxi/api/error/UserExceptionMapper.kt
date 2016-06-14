package br.com.alex.taxi.api.error

import br.com.alex.taxi.model.User
import retrofit2.Response
import retrofit2.adapter.rxjava.HttpException
import rx.Observable

/**
 * Created by alex on 13/06/16.
 */
object UserExceptionMapper {

    fun fromException(httpException: Throwable?): Observable<out User>? {
        return if (httpException is HttpException) {

            when (httpException.code()) {
                404 -> {
                    Observable.error(UserNotFoundException())}
                400 -> {
                    Observable.error(UserInvalidException())}
                else -> {
                    Observable.error(httpException)}
            }

        } else {
            Observable.error<User>(httpException)
        }
    }

    fun fromResponse(response: Response<Void>): Response<Void> {

        when (response.code()) {
            404 -> { throw UserNotFoundException()
            }
            400 -> { throw UserInvalidException()
            }
        }

        return response
    }
}