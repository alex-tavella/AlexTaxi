package br.com.alex.taxi.androidapp.map

import android.content.Context
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import br.com.alex.taxi.androidapp.R
import br.com.alex.taxi.api.RequestFactory
import br.com.alex.taxi.model.Taxi
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by alex on 07/06/16.
 */
class TaxiMapController(val context: Context) : GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private var mMap: GoogleMap? = null

    private val mGoogleApiClient: GoogleApiClient by lazy {
        GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
    }

    private var mLastLocation: LatLng? = null

    private var mRidePoint: Marker? = null

    private var mCurrentTaxis: MutableList<Marker> = mutableListOf()

    private var mTaxiMonitorSubscription: Subscription? = null

    companion object {
        private val TAG = "TaxiMapController"

        private val MONITOR_FREQUENCY = 1L
    }

    fun initialize(map: GoogleMap) {
        Log.d(TAG, "Initializing taxi map controller")
        configureMap(map)
        mMap = map

        if (mGoogleApiClient.isConnected) {
            proceedAfterConnected()
        } else {
            mGoogleApiClient.connect()
        }
    }

    fun release() {
        Log.d(TAG, "Releasing taxi map controller")
        cancelTaxisMonitor()
        mCurrentTaxis.clear()
        mRidePoint?.remove()
        mRidePoint = null
        mMap?.setOnMapLongClickListener(null)
        mMap?.clear()
        mMap = null
        if (mGoogleApiClient.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
            mGoogleApiClient.disconnect()
        }
    }

    private fun configureMap(map: GoogleMap) {
        map.isMyLocationEnabled = true

        // Calculate ActionBar height
        val tv = TypedValue();
        var paddingTop: Int = context.resources.getDimension(R.dimen.map_padding_top).toInt()
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            val actionBarHeight = TypedValue
                    .complexToDimensionPixelSize(tv.data, context.resources.displayMetrics);
            paddingTop += actionBarHeight
        }

        map.setPadding(0, paddingTop, 0, 0)
        map.setOnMapLongClickListener { setRidePoint(it) }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.d(TAG, "Google api client connection failed - ${connectionResult.errorMessage}")
    }

    override fun onConnected(p0: Bundle?) {
        proceedAfterConnected()
    }

    override fun onLocationChanged(location: Location?) {
        Log.d(TAG, "Last known location updated")
        updateCurrentLocation(location!!)
    }

    private fun proceedAfterConnected() {
        Log.d(TAG, "Google api client connected")
        val lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient)

        Log.d(TAG, "Last location is $lastLocation")

        updateCurrentLocation(lastLocation, true)

        if (mRidePoint == null) {
            Log.d(TAG, "Setting ride point to last known location")
            setRidePoint(lastLocation.getLatLng())
        }

        startMonitoringTaxis()
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                LocationRequest(), this)
    }

    private fun setRidePoint(latLng: LatLng) {
        mRidePoint?.remove()
        mRidePoint = mMap?.addMarker(MarkerOptions().position(latLng).draggable(true))
    }

    private fun updateCurrentLocation(location: Location, moveCamera: Boolean = false) {
        mLastLocation = location.getLatLng()

        if (moveCamera) {
            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(mLastLocation, 15f))
        }
    }

    fun focusRidePoint() {
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(mRidePoint?.position, 15f))
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.d(TAG, "Google api client connection suspended.")
    }

    fun startMonitoringTaxis() {
        Log.d(TAG, "Started to monitor taxis.")
        mTaxiMonitorSubscription = Observable.interval(0, MONITOR_FREQUENCY, TimeUnit.MINUTES)
                .observeOn(AndroidSchedulers.mainThread())
                .map { mMap?.projection!!.visibleRegion?.latLngBounds!! }
                .observeOn(Schedulers.io())
                .flatMap {
                    RequestFactory.Builder().build().
                            newLastLocationsRequest(it.southwest.toLocationString(),
                            it.northeast.toLocationString())
                }
                .doOnError { Log.w(TAG, "Error when fetching taxis location - ${it?.message}") }
                .onErrorResumeNext { Observable.empty() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({ placeTaxis(it) })
    }

    fun cancelTaxisMonitor() {
        if (!(mTaxiMonitorSubscription?.isUnsubscribed ?: true)) {
            mTaxiMonitorSubscription?.unsubscribe()
        }
    }

    private fun placeTaxis(taxis: List<Taxi>) {
        val availableTaxiBD = BitmapDescriptorFactory.fromResource(R.mipmap.marker_taxi_available)
        val unavailableTaxiBD = BitmapDescriptorFactory.fromResource(R.mipmap.marker_taxi_unavailable)
        clearTaxis()
        mCurrentTaxis.addAll(taxis
                .map {
                    MarkerOptions().position(LatLng(it.lat, it.lng))
                            .icon(if (it.available) availableTaxiBD else unavailableTaxiBD)
                }
                .map { mMap?.addMarker(it)!! })
    }

    fun clearTaxis() {
        mCurrentTaxis.forEach { it.remove() }
        mCurrentTaxis.clear()
    }

    fun getRidePoint(): LatLng? {
        return mRidePoint?.position
    }
}

fun Location.getLatLng(): LatLng {
    return LatLng(latitude, longitude)
}

fun LatLng.toLocationString(): String {
    return "$latitude,$longitude"
}