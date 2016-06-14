package br.com.alex.taxi.androidapp.welcome

import android.content.Intent
import android.content.res.TypedArray
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioGroup
import br.com.alex.taxi.androidapp.R
import br.com.alex.taxi.androidapp.commons.view.displayErrorMessage
import br.com.alex.taxi.androidapp.home.HomeActivity
import br.com.alex.taxi.androidapp.userprofile.UserProfileProvider
import br.com.alex.taxi.api.RequestFactory
import br.com.alex.taxi.model.User
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

class WelcomeActivity : AppCompatActivity(), UserValidationListener {

    private val mProgressBar: ProgressBar by lazy { findViewById(R.id.progressBar) as ProgressBar }

    private var mWizardPagerAdapter: WizardPagerAdapter? = null

    private val mViewPager: ViewPager by lazy { findViewById(R.id.container) as ViewPager }

    private val mRadioGroup: RadioGroup by lazy { findViewById(R.id.rg_wizard) as RadioGroup }

    private val mStartButton: Button by lazy { findViewById(R.id.btn_start) as Button}

    private var mUserToRegister: User? = null

    private var mRegisterSubscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val toolbar = findViewById(R.id.toolbar) as Toolbar?
        setSupportActionBar(toolbar)

        mStartButton.setOnClickListener({ v -> registerUser() })

        val wizardRadioGroupIds = resources.obtainTypedArray(R.array.wizard_radio_group_ids)
        val wizardTitleIds = resources.obtainTypedArray(R.array.wizard_title_text_ids)
        val wizardDetailIds = resources.obtainTypedArray(R.array.wizard_detail_text_ids)
        val wizardIconIds = resources.obtainTypedArray(R.array.wizard_icon_ids)

        val numberOfPages = wizardRadioGroupIds.length()

        mWizardPagerAdapter = WizardPagerAdapter(supportFragmentManager,
                buildWizardPages(wizardTitleIds, wizardDetailIds, wizardIconIds,
                        numberOfPages - 1))

        mViewPager.adapter = mWizardPagerAdapter
        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                mRadioGroup.check(wizardRadioGroupIds.getResourceId(position, R.id.rb_wizard_1))

                if (position == numberOfPages - 1) {
                    mStartButton.visibility = View.VISIBLE
                } else {
                    mStartButton.visibility = View.GONE
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float,
                                        positionOffsetPixels: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }

    override fun onStop() {
        displayProgress(false)
        if (!(mRegisterSubscription?.isUnsubscribed ?: true)) {
            mRegisterSubscription?.unsubscribe()
        }
        super.onStop()
    }

    private fun registerUser() {
        mRegisterSubscription = Observable.just(mUserToRegister!!)
                .doOnSubscribe { displayProgress(true) }
                .observeOn(Schedulers.io())
                .flatMap { RequestFactory.Builder().build().newRegisterUserRequest(it) }
                .flatMap {
                    UserProfileProvider.getInstance(applicationContext)
                            .saveCurrentUserProfile(it)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { displayProgress(false) }
                .doOnCompleted { displayProgress(false) }
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ startActivity(Intent(this, HomeActivity::class.java)) },
                        { displayErrorMessage(it?.message!!) })
    }

    private fun displayProgress(displayProgress: Boolean) {
        mProgressBar.visibility = if (displayProgress) View.VISIBLE else View.GONE
    }

    override fun onUserValid(user: User) {
        mUserToRegister = user
        mStartButton.isEnabled = true
    }

    override fun onUserInvalid() {
        mStartButton.isEnabled = false
    }

    fun buildWizardPages(wizardTitleIds: TypedArray,
                         wizardDetailIds: TypedArray,
                         wizardIconIds: TypedArray, size: Int): ArrayList<WizardPageResource> {
        val pageResources = ArrayList<WizardPageResource>()
        val defaultTitle = R.string.welcome_text
        val defaultDetail = R.string.welcome_detail_text
        val defaultIcon = R.mipmap.ic_launcher

        for (i in 0..size - 1) {
            pageResources.add(WizardPageResource(wizardIconIds.getResourceId(i, defaultIcon),
                    wizardTitleIds.getResourceId(i, defaultTitle),
                    wizardDetailIds.getResourceId(i, defaultDetail)))
        }

        return pageResources
    }
}
