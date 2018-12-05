package com.github.grzegorzekkk.serverstatusnotifier.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel.SrvConnDetails

class SrvConnDetailsDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SrvConnDetailsContract.SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

    fun fetchServersFromDb(): List<SrvConnDetails> {
        val srvConnDetailsList = mutableListOf<SrvConnDetails>()

        val db = readableDatabase
        val dbCursor = db.query(SrvConnDetailsContract.ConnectionDetailsEntry.TABLE_NAME, null, null, null, null, null, null)
        with(dbCursor) {
            while (moveToNext()) {
                val serverAddress = getString(getColumnIndexOrThrow(SrvConnDetailsContract.ConnectionDetailsEntry.COLUMN_NAME_ADDRESS))
                val serverPort = getInt(getColumnIndexOrThrow(SrvConnDetailsContract.ConnectionDetailsEntry.COLUMN_NAME_PORT))
                val serverPassword = getString(getColumnIndexOrThrow(SrvConnDetailsContract.ConnectionDetailsEntry.COLUMN_NAME_PASSWORD))
                val srvConnRecord = SrvConnDetails(serverAddress, serverPort, serverPassword)
                srvConnDetailsList.add(srvConnRecord)
            }
        }

        return srvConnDetailsList
    }

    fun saveServerConnDetails(srvConnDetails: SrvConnDetails) {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(SrvConnDetailsContract.ConnectionDetailsEntry.COLUMN_NAME_ADDRESS, srvConnDetails.address)
            put(SrvConnDetailsContract.ConnectionDetailsEntry.COLUMN_NAME_PORT, srvConnDetails.port)
            put(SrvConnDetailsContract.ConnectionDetailsEntry.COLUMN_NAME_PASSWORD, srvConnDetails.password)
        }

        val newRowId = db?.insert(SrvConnDetailsContract.ConnectionDetailsEntry.TABLE_NAME, null, values)
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "ServerStatusNotifier.db"
    }
}