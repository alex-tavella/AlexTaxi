package br.com.alex.taxi.api.error

/**
 * Created by alex on 13/06/16.
 */
class RideException : Exception {

    constructor(errorMessage: String)

    constructor(errorMessage: String, throwable: Throwable)

    constructor(throwable: Throwable)
}