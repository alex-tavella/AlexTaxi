package br.com.alex.taxi.androidapp.map

import com.google.android.gms.maps.model.LatLng
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by alex on 13/06/16.
 */
class LatLngConvertTest {

    @Test
    fun convertSuccessful() {
        val locationString = LatLng(0.0, 0.0).toLocationString()
        assertEquals("0.0,0.0", locationString)
    }

    @Test
    fun convertMinusZero() {
        val locationString = LatLng(-0.0, -0.0).toLocationString()
        assertEquals("-0.0,-0.0", locationString)
    }
}