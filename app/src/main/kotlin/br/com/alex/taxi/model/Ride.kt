package br.com.alex.taxi.model

import com.google.gson.annotations.SerializedName

/**
 * Created by alex on 02/06/16.
 */
data class Ride(@SerializedName("id") val id: Long,
                @SerializedName("msg") val msg: String) {
}