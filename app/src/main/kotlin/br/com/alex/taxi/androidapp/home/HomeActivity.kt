package br.com.alex.taxi.androidapp.home

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import br.com.alex.taxi.androidapp.R
import br.com.alex.taxi.androidapp.commons.OnUserRegisteredListener
import br.com.alex.taxi.androidapp.commons.view.AlertDialogFragment
import br.com.alex.taxi.androidapp.commons.view.displayErrorMessage
import br.com.alex.taxi.androidapp.home.navigation.AboutFragment
import br.com.alex.taxi.androidapp.home.navigation.HomeFragment
import br.com.alex.taxi.androidapp.home.navigation.ProfileFragment
import br.com.alex.taxi.androidapp.map.TaxiMapController
import br.com.alex.taxi.androidapp.userprofile.UserProfileProvider
import br.com.alex.taxi.api.RequestFactory
import br.com.alex.taxi.model.User
import com.google.android.gms.maps.GoogleMap
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.io.IOException

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnMapListener, AlertDialogFragment.OnSubmitListener,
        HomeFragment.OnOrderTaxiFabClickListener, OnUserRegisteredListener {

    companion object {
        val EXTRA_CURRENT_CONTENT = "current_content"
    }

    private val mTaxiMapController: TaxiMapController by lazy { TaxiMapController(applicationContext) }

    private val mGeocoder: Geocoder by lazy { Geocoder(applicationContext) }

    private val mDrawer: DrawerLayout? by lazy { findViewById(R.id.drawer_layout) as DrawerLayout? }

    private val mNavigationView: NavigationView? by lazy { findViewById(R.id.nav_view) as NavigationView? }

    private val mNameTextView: TextView by lazy { mNavigationView?.getHeaderView(0)?.findViewById(R.id.nav_header_username) as TextView }

    private val mProgress: ProgressBar? by lazy { findViewById(R.id.progressBar) as ProgressBar? }

    private val mSubscriptions: CompositeSubscription = CompositeSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val toolbar = findViewById(R.id.toolbar) as Toolbar?
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        mDrawer?.addDrawerListener(toggle)
        toggle.syncState()

        mNavigationView?.setNavigationItemSelectedListener(this)
        mNavigationView?.setCheckedItem(R.id.nav_home)

        if (savedInstanceState != null) {
            switchContentView(supportFragmentManager.getFragment(savedInstanceState, EXTRA_CURRENT_CONTENT))
        } else {
            switchContentView(HomeFragment())
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        supportFragmentManager.putFragment(outState, EXTRA_CURRENT_CONTENT,
                supportFragmentManager.findFragmentById(R.id.content_home))
        super.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        mSubscriptions.add(UserProfileProvider.getInstance(applicationContext)
                .getCurrentUserProfile()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ mNameTextView.text = it.name },
                        { Log.d(localClassName, "Error when fetching user profile - ${it?.message}") }))
    }

    override fun onUserRegistered(user: User) {
        mNameTextView.text = user.name
    }

    override fun onStop() {
        mSubscriptions.clear()
        displayProgress(false)
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mTaxiMapController.release()
    }

    override fun onBackPressed() {
        if (mDrawer!!.isDrawerOpen(GravityCompat.START)) {
            mDrawer?.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                switchContentView(HomeFragment())
            }
            R.id.nav_profile -> {
                switchContentView(ProfileFragment())
            }
            R.id.nav_about -> {
                switchContentView(AboutFragment())
            }
            R.id.nav_order_taxi -> {
                confirmAddressAndRequestRide()
            }
            R.id.nav_exit -> {
                exit()
            }
        }

        mDrawer?.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mNavigationView?.menu?.findItem(R.id.nav_order_taxi)?.isEnabled = true
        mTaxiMapController.initialize(googleMap!!)
    }

    override fun onMapReleased() {
        mNavigationView?.menu?.findItem(R.id.nav_order_taxi)?.isEnabled = false
        mTaxiMapController.release()
    }

    override fun onDialogSubmit() {
        mSubscriptions.add(Observable.just(null)
                .doOnSubscribe { displayProgress(true) }
                .observeOn(Schedulers.io())
                .flatMap{ RequestFactory.Builder().build().newRideRequestRequest() }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { Log.d(localClassName, "Error requesting ride - ${it?.message}") }
                .doOnCompleted { displayProgress(false) }
                .subscribe ({ Toast.makeText(this, it.msg, Toast.LENGTH_SHORT).show() }
                        , { displayErrorMessage(getString(R.string.dialog_content_ride_request_error)) }))
    }

    override fun onDialogOk() {
        // do nothing
    }

    override fun onDialogCancel() {
        // do nothing
    }

    override fun onOrderRideClicked() {
        confirmAddressAndRequestRide()
    }

    private fun confirmAddressAndRequestRide() {
        val ridePoint = mTaxiMapController.getRidePoint()
        if (ridePoint != null) {
            mSubscriptions.add(Observable.just(null)
                    .doOnSubscribe {
                        displayProgress(true)
                        mTaxiMapController.focusRidePoint()
                    }
                    .observeOn(Schedulers.io())
                    .flatMap { Observable.create<Address> { subscriber ->
                        subscriber.onStart()

                        if (!subscriber.isUnsubscribed) {
                            try {
                                subscriber.onNext(mGeocoder
                                        .getFromLocation(ridePoint.latitude,
                                                ridePoint.longitude, 1)[0])
                            } catch (e: IOException) {
                                subscriber.onError(e)
                            } finally {
                                subscriber.onCompleted()
                            }
                        } else {
                            subscriber.onCompleted()
                        }
                    } }
                    .map { it.getAddressLine(0) }
                    .doOnNext { Log.d(localClassName, it) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnCompleted { displayProgress(false) }
                    .subscribe(
                            {
                                AlertDialogFragment.Factory.createSubmitDialog(this,
                                        getString(R.string.dialog_title_submit_ride_request),
                                        getString(R.string.dialog_content_submit_ride_request, it))
                                        .show(supportFragmentManager, AlertDialogFragment.TAG_SUBMIT_DIALOG)
                            },
                            {
                                displayErrorMessage(getString(R.string.dialog_content_location_address_error))
                            }))
        }
    }

    private fun exit() {
        UserProfileProvider.getInstance(applicationContext).clear()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ finish() }, { finish() })
    }

    private fun displayProgress(displayProgress: Boolean) {
        mProgress?.visibility = if (displayProgress) View.VISIBLE else View.GONE
    }

    private fun switchContentView(fragment: Fragment) {

        val currentFragment = supportFragmentManager.findFragmentById(R.id.content_home)

        if (currentFragment == null || currentFragment.javaClass != fragment.javaClass) {
            supportFragmentManager.beginTransaction().replace(R.id.content_home, fragment).commit()
        }
    }
}
