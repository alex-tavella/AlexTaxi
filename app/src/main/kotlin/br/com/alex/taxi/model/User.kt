package br.com.alex.taxi.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by alex on 02/06/16.
 */
data class User(@SerializedName("id") val id: Long,
                @SerializedName("name") var name: String): Parcelable {

    companion object {

        @JvmField
        val CREATOR = object : Parcelable.Creator<User> {
            override fun createFromParcel(`in`: Parcel): User {
                return User(`in`)
            }

            override fun newArray(size: Int): Array<User?> {
                return arrayOfNulls(size)
            }
        }
    }

    constructor(`in`: Parcel): this(`in`.readLong(), `in`.readString())

    override fun writeToParcel(`out`: Parcel?, flags: Int) {
        `out`?.writeLong(id)
        `out`?.writeString(name)
    }

    override fun describeContents() = 0

    fun isValid(): Boolean {
        return !name.isNullOrBlank()
    }
}