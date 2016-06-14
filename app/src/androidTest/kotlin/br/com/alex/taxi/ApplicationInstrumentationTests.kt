package br.com.alex.taxi

import br.com.alex.taxi.androidapp.LauncherActivityTests
import br.com.alex.taxi.androidapp.home.HomeTestSuite
import br.com.alex.taxi.androidapp.userprofile.UserProfileTest
import br.com.alex.taxi.androidapp.welcome.WelcomeTestSuite
import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * Created by alex on 13/06/16.
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
        UserProfileTest::class,
        LauncherActivityTests::class,
        WelcomeTestSuite::class,
        HomeTestSuite::class)
class ApplicationInstrumentationTests {
}