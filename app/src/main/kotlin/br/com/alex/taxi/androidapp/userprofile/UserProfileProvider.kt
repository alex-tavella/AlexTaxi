package br.com.alex.taxi.androidapp.userprofile

import android.content.Context
import android.content.SharedPreferences
import br.com.alex.taxi.model.User
import com.google.gson.Gson
import rx.Observable

/**
 * Created by alex on 05/06/16.
 */
interface UserProfileProvider {

    object Factory {
        fun createDefault(context: Context): UserProfileProvider {
            return DefaultUserProfileProvider(context)
        }
    }

    companion object Singleton {
        private var singletonInstance: UserProfileProvider? = null

        fun getInstance(context: Context): UserProfileProvider {
            if (singletonInstance == null) {
                singletonInstance = Factory.createDefault(context)
            }
            return singletonInstance!!
        }
    }

    fun getCurrentUserProfile(): Observable<User>
    fun saveCurrentUserProfile(user: User): Observable<Void>
    fun clear(): Observable<Void>
}

class DefaultUserProfileProvider(context: Context) : UserProfileProvider {

    companion object {
        private val PROVIDER_NAME = "user-profile"

        private val FIELD_USER = "user"
    }

    private val mPreferences: SharedPreferences

    private val mConverter: Gson

    init {
        mPreferences = context.getSharedPreferences(PROVIDER_NAME, Context.MODE_PRIVATE)
        mConverter = Gson()
    }

    override fun getCurrentUserProfile(): Observable<User> {
        return Observable.create<String> { subscriber ->
            subscriber.onStart()

            if (!subscriber.isUnsubscribed) {
                val userString = mPreferences.getString(FIELD_USER, "")
                if (userString.isNullOrEmpty()) {
                    subscriber.onError(UserProfileNotFoundException())
                } else {
                    subscriber.onNext(userString)
                }
            }

            subscriber.onCompleted()
        }.map { mConverter.fromJson(it, User::class.java) }
    }

    override fun saveCurrentUserProfile(user: User): Observable<Void> {
        return Observable.create { subscriber ->
            subscriber.onStart()
            if (!subscriber.isUnsubscribed) {
                mPreferences.edit().putString(FIELD_USER, mConverter.toJson(user)).commit()
                subscriber.onNext(null)
            }
            subscriber.onCompleted()
        }
    }

    override fun clear(): Observable<Void> {
        return Observable.create { subscriber ->
            subscriber.onStart()
            if (!subscriber.isUnsubscribed) {
                mPreferences.edit().clear().commit()
                subscriber.onNext(null)
            }
            subscriber.onCompleted()
        }
    }

}