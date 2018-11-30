package com.github.grzegorzekkk.serverstatusnotifier.database

import android.os.Parcel
import android.os.Parcelable

data class SrvConnDetails(val address: String, val port: Int, val password: String) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(address)
        parcel.writeInt(port)
        parcel.writeString(password)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SrvConnDetails> {
        override fun createFromParcel(parcel: Parcel): SrvConnDetails {
            return SrvConnDetails(parcel)
        }

        override fun newArray(size: Int): Array<SrvConnDetails?> {
            return arrayOfNulls(size)
        }
    }

}