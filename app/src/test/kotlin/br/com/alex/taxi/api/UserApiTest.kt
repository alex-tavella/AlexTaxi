package br.com.alex.taxi.api

import br.com.alex.taxi.api.error.UserInvalidException
import br.com.alex.taxi.api.error.UserNotFoundException
import br.com.alex.taxi.model.User
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
 * Created by alex on 11/06/16.
 */
class UserApiTest {

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
    fun testRegisterSuccess() {
        mockWebServer.enqueue(MockResponse().setResponseCode(201).setHeader("Location", "GET-URL"))
        mockWebServer.enqueue(MockResponse().setResponseCode(200)
                .setBody("{\"name\": \"Tester\", \"id\":1337}"))

        val testSubscriber = TestSubscriber<User>()

        requestFactory!!.newRegisterUserRequest(User(-1L, "Tester"))
                .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(User(1337L, "Tester"))
        testSubscriber.assertCompleted()
    }

    @Test
    fun testRegisterUserInvalid() {
        mockWebServer.enqueue(MockResponse().setResponseCode(400))

        val testSubscriber = TestSubscriber<User>()

        requestFactory!!.newRegisterUserRequest(User(-1L, "Tester"))
                .subscribe(testSubscriber)

        testSubscriber.assertError(UserInvalidException::class.java)
    }

    @Test
    fun testRegisteredUserNotFound() {
        mockWebServer.enqueue(MockResponse().setResponseCode(201)
                .setHeader("Location", "taxi.com/api/user/1337"))
        mockWebServer.enqueue(MockResponse().setResponseCode(404))

        val testSubscriber = TestSubscriber<User>()

        requestFactory!!.newRegisterUserRequest(User(-1L, "Tester"))
                .subscribe(testSubscriber)

        testSubscriber.assertError(UserNotFoundException::class.java)
    }

    @Test
    fun testUpdateUserSuccess() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200)
                .setBody("{\"name\": \"Tester\", \"id\":1337}"))

        val testSubscriber = TestSubscriber<User>()

        requestFactory!!.newUpdateUserRequest(User(1337L, "Tester"))
                .subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(User(1337, "Tester"))
        testSubscriber.assertCompleted()
    }

    @Test
    fun testUpdateUserInvalid() {
        mockWebServer.enqueue(MockResponse().setResponseCode(400))

        val testSubscriber = TestSubscriber<User>()

        requestFactory!!.newUpdateUserRequest(User(1337L, "Tester"))
                .subscribe(testSubscriber)

        testSubscriber.assertError(UserInvalidException::class.java)
    }

    @Test
    fun testUpdateUserNotFound() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))

        val testSubscriber = TestSubscriber<User>()

        requestFactory!!.newUpdateUserRequest(User(1337L, "Tester"))
                .subscribe(testSubscriber)

        testSubscriber.assertError(UserNotFoundException::class.java)
    }

    @After
    fun release() {
        mockWebServer.shutdown()
    }
}