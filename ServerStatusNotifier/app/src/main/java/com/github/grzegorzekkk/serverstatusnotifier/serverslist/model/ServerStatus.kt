package com.github.grzegorzekkk.serverstatusnotifier.serverslist.model

import android.os.Parcel
import android.os.Parcelable

data class ServerStatus(val serverName: String, val isOnline: Boolean) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readByte() != 0.toByte())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(serverName)
        parcel.writeByte(if (isOnline) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ServerStatus> {
        override fun createFromParcel(parcel: Parcel): ServerStatus {
            return ServerStatus(parcel)
        }

        override fun newArray(size: Int): Array<ServerStatus?> {
            return arrayOfNulls(size)
        }
    }

}