package br.com.alex.taxi.api

import br.com.alex.taxi.model.Taxi
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
class LastLocationsApiTest {

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
    fun testLastLocationsSuccess() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200)
                .setBody("[{\"latitude\":-23.60810717,\"longitude\":-46.67500346,\"driverId\":5997,\"driverAvailable\":true}," +
                        "{\"latitude\":-23.59065045044675,\"longitude\":-46.68837101634931,\"driverId\":63446,\"driverAvailable\":true}," +
                        "{\"latitude\":-23.60925506,\"longitude\":-46.69390415,\"driverId\":1982,\"driverAvailable\":true}," +
                        "{\"latitude\":-23.599871666666665,\"longitude\":-46.680903333333326,\"driverId\":9106,\"driverAvailable\":true}]"))

        val testSubscriber = TestSubscriber<List<Taxi>>()

        requestFactory!!.newLastLocationsRequest("-23.612474,-46.702746", "-23.589548,-46.673392")
                .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertCompleted()
    }

    @After
    fun release() {
        mockWebServer.shutdown()
    }
}