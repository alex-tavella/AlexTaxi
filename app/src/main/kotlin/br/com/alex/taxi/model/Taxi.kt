package br.com.alex.taxi.model

import com.google.gson.annotations.SerializedName

/**
 * Created by alex on 02/06/16.
 */
data class Taxi(@SerializedName("driverId") val id: String,
                @SerializedName("latitude") val lat: Double,
                @SerializedName("longitude") val lng: Double,
                @SerializedName("driverAvailable") val available: Boolean) {
}