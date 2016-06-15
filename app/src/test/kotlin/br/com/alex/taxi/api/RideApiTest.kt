package br.com.alex.taxi.api

import br.com.alex.taxi.api.error.RideException
import br.com.alex.taxi.api.error.RideNotFoundException
import br.com.alex.taxi.model.Ride
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.observers.TestSubscriber

/**
 * Created by alex on 13/06/16.
 */
class RideApiTest {

    val mockWebServer: MockWebServer = MockWebServer()

    var requestFactory: RequestFactory? = null

    @Before
    fun setup() {

        val retrofit = Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()

        requestFactory = RequestFactory.Builder()
                .setLastLocationsServiceEndpoint(retrofit.create(LastLocationsService::class.java))
                .setTaxisServiceEndpoint(retrofit.create(TaxisService::class.java))
                .build()
    }

    @Test
    fun testRequestRideSuccess() {
        mockWebServer.enqueue(MockResponse().setResponseCode(201)
                .setHeader("Location", "taxi.com/api/ride/1337"))
        mockWebServer.enqueue(MockResponse().setResponseCode(200)
                .setBody("{\"msg\": \"Driver is on his way\", \"id\":1337}"))

        val testSubscriber = TestSubscriber<Ride>()

        requestFactory!!.newRideRequestRequest().subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertCompleted()
    }

    @Test
    fun testRequestRideFail() {
        mockWebServer.enqueue(MockResponse().setResponseCode(410))

        val testSubscriber = TestSubscriber<Ride>()

        requestFactory!!.newRideRequestRequest().subscribe(testSubscriber)

        testSubscriber.assertError(RideException::class.java)
    }

    @Test
    fun testRequestRideNotFound() {
        mockWebServer.enqueue(MockResponse().setResponseCode(201)
                .setHeader("Location", "taxi.com/api/ride/1337"))
        mockWebServer.enqueue(MockResponse().setResponseCode(404))

        val testSubscriber = TestSubscriber<Ride>()

        requestFactory!!.newRideRequestRequest().subscribe(testSubscriber)

        testSubscriber.assertError(RideNotFoundException::class.java)
    }

    @After
    fun release() {
        mockWebServer.shutdown()
    }
}