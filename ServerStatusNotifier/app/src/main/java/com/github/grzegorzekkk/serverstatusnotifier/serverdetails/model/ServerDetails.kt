package com.github.grzegorzekkk.serverstatusnotifier.serverdetails.model

import android.os.Parcel
import android.os.Parcelable
import com.github.grzegorzekkk.serverstatusnotifier.database.SrvConnDetails
import com.github.grzegorzekkk.serverstatusnotifier.serverslist.model.ServerStatus

data class ServerDetails(val serverStatus: ServerStatus, var srvConnDetails: SrvConnDetails, val playersCount: Int) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readParcelable(ServerStatus::class.java.classLoader),
            parcel.readParcelable(SrvConnDetails::class.java.classLoader),
            parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(serverStatus, flags)
        parcel.writeParcelable(srvConnDetails, flags)
        parcel.writeInt(playersCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ServerDetails> {
        const val SERIAL_NAME = "serverDetails"

        override fun createFromParcel(parcel: Parcel): ServerDetails {
            return ServerDetails(parcel)
        }

        override fun newArray(size: Int): Array<ServerDetails?> {
            return arrayOfNulls(size)
        }
    }
}
