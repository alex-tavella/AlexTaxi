package br.com.alex.taxi.api

import br.com.alex.taxi.api.error.RideExceptionMapper
import br.com.alex.taxi.api.error.UserExceptionMapper
import br.com.alex.taxi.model.Ride
import br.com.alex.taxi.model.Taxi
import br.com.alex.taxi.model.User
import rx.Observable

/**
 * Created by alex on 09/06/16.
 */
class RequestFactory(val taxisService: TaxisService,
                              val lastLocationsService: LastLocationsService) {

    class Builder {

        var taxisService: TaxisService? = null

        var lastLocationsService: LastLocationsService? = null

        fun setTaxisServiceEndpoint(taxisService: TaxisService): Builder {
            this.taxisService = taxisService
            return this
        }

        fun setLastLocationsServiceEndpoint(lastLocationsService: LastLocationsService): Builder {
            this.lastLocationsService = lastLocationsService
            return this
        }

        fun build(): RequestFactory {

            if (taxisService == null) {
                taxisService = TaxisService.Factory.createDefault()
            }

            if (lastLocationsService == null) {
                lastLocationsService = LastLocationsService.Factory.createDefault()
            }

            return RequestFactory(taxisService!!, lastLocationsService!!)
        }
    }

    companion object {
        private val HEADER_LOCATION = "Location"
    }

    fun newLastLocationsRequest(sw: String, ne: String): Observable<List<Taxi>> {
        return lastLocationsService.getTaxisInRegion(sw, ne)
    }

    fun newRegisterUserRequest(user: User): Observable<User> {
        return taxisService.registerUser(user.name)
                .map{ UserExceptionMapper.fromResponse(it) }
                .map { it.headers().get(HEADER_LOCATION) }
                .flatMap { taxisService.getUser(it) }
                .onErrorResumeNext { UserExceptionMapper.fromException(it) }
    }

    fun newUpdateUserRequest(user: User): Observable<User> {
        return taxisService.editUser(user.id, user.name)
                .onErrorResumeNext { UserExceptionMapper.fromException(it) }
    }

    fun newRideRequestRequest(): Observable<Ride> {
        return taxisService.requestRide()
                .map { RideExceptionMapper.fromResponse(it) }
                .map { it.headers().get(HEADER_LOCATION) }
                .flatMap { taxisService.getRide(it) }
                .onErrorResumeNext { RideExceptionMapper.fromException(it) }
    }
}