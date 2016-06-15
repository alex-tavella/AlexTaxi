package br.com.alex.taxi.androidapp.home.navigation

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import br.com.alex.taxi.androidapp.R
import br.com.alex.taxi.androidapp.commons.OnUserRegisteredListener
import br.com.alex.taxi.androidapp.commons.view.displayErrorMessage
import br.com.alex.taxi.androidapp.userprofile.UserProfileProvider
import br.com.alex.taxi.api.RequestFactory
import br.com.alex.taxi.model.User
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

/**
 * Created by alex on 07/06/16.
 */
class ProfileFragment : Fragment(), TextWatcher {

    private val mSubscriptions: CompositeSubscription = CompositeSubscription()

    private val mEtUsername: EditText? by lazy { view?.findViewById(R.id.et_username) as EditText? }

    private val mProgressBar: ProgressBar? by lazy { view?.findViewById(R.id.progressBar) as ProgressBar? }

    private val mSubmitButton: Button? by lazy { view?.findViewById(R.id.btn_submit) as Button? }

    private var mUser: User? = null

    private var mUserRegistrationListener: OnUserRegisteredListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is OnUserRegisteredListener) {
            mUserRegistrationListener = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater?.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mEtUsername?.addTextChangedListener(this)
        mSubmitButton?.setOnClickListener {
            mSubscriptions.add(Observable.just(mUser!!)
                    .doOnSubscribe { displayProgress(true) }
                    .observeOn(Schedulers.io())
                    .flatMap { RequestFactory.Builder().build().newUpdateUserRequest(it) }
                    .flatMap {
                        UserProfileProvider.getInstance(activity.applicationContext)
                                .saveCurrentUserProfile(it)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError { displayProgress(false) }
                    .doOnNext { displayProgress(false) }
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                mUserRegistrationListener?.onUserRegistered(mUser!!)
                                Toast.makeText(context,
                                        getString(R.string.toast_user_updated_successfully),
                                        Toast.LENGTH_SHORT).show()
                            },
                            { displayErrorMessage(getString(R.string.dialog_content_unexpected_error)) }))
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.menu_profile)
        }
    }

    override fun onStart() {
        super.onStart()
        mSubscriptions.add(Observable.just(null)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { displayProgress(true) }
                .observeOn(Schedulers.io())
                .flatMap {
                    UserProfileProvider.getInstance(activity.applicationContext)
                            .getCurrentUserProfile()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { displayProgress(false) }
                .subscribe({
                    mUser = it
                    mEtUsername?.setText(mUser?.name)
                    validateUser()
                },
                        { displayErrorMessage(getString(R.string.dialog_content_unexpected_error)) }))
    }

    override fun onStop() {
        mSubscriptions.clear()
        super.onStop()
    }

    private fun displayProgress(display: Boolean) {
        mProgressBar?.visibility = if (display) View.VISIBLE else View.GONE
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun afterTextChanged(text: Editable?) {
        mUser?.name = text.toString()
        validateUser()
    }

    private fun validateUser() {
        mSubmitButton?.isEnabled = mUser?.isValid() ?: false
    }
}