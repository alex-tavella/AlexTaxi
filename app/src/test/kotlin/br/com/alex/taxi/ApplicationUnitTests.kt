package br.com.alex.taxi

import br.com.alex.taxi.androidapp.map.LatLngConvertTest
import br.com.alex.taxi.api.ApiTestSuite
import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * Created by alex on 13/06/16.
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
        LatLngConvertTest::class,
        ApiTestSuite::class)
class ApplicationUnitTests {
}