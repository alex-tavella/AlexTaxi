package br.com.alex.taxi.androidapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import br.com.alex.taxi.androidapp.home.HomeActivity
import br.com.alex.taxi.androidapp.userprofile.UserProfileProvider
import br.com.alex.taxi.androidapp.welcome.WelcomeActivity
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class LauncherActivity : AppCompatActivity() {

    private var mSubscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mSubscription = UserProfileProvider.getInstance(applicationContext).getCurrentUserProfile()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({startActivity(Intent(this, HomeActivity::class.java))},
                        {startActivity(Intent(this, WelcomeActivity::class.java))});
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!(mSubscription?.isUnsubscribed ?: true)) {
            mSubscription?.unsubscribe()
        }
    }
}
