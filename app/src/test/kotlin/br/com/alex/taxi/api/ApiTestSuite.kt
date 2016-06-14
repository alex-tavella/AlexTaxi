package br.com.alex.taxi.api

import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * Created by alex on 13/06/16.
 */
@RunWith(Suite::class)
@Suite.SuiteClasses (
        UserApiTest::class,
        RideApiTest::class,
        LastLocationsApiTest::class)
class ApiTestSuite {
}