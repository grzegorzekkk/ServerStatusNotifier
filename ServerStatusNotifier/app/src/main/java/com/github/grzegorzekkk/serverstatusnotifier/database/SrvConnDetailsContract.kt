package com.github.grzegorzekkk.serverstatusnotifier.database

import android.provider.BaseColumns

object SrvConnDetailsContract {
    const val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${ConnectionDetailsEntry.TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                    "${ConnectionDetailsEntry.COLUMN_NAME_ADDRESS} TEXT," +
                    "${ConnectionDetailsEntry.COLUMN_NAME_PORT} INTEGER," +
                    "${ConnectionDetailsEntry.COLUMN_NAME_PASSWORD} TEXT)"

    const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${ConnectionDetailsEntry.TABLE_NAME}"

    object ConnectionDetailsEntry : BaseColumns {
        const val TABLE_NAME = "server_conn_details"
        const val COLUMN_NAME_ADDRESS = "address"
        const val COLUMN_NAME_PORT = "port"
        const val COLUMN_NAME_PASSWORD = "password"
    }
}