package com.github.grzegorzekkk.serverstatusnotifier.database

import android.provider.BaseColumns

object AppSettingsContract {
    const val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${AppSettingsEntry.TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                    "${AppSettingsEntry.COLUMN_NAME_UUID} TEXT)"

    const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${AppSettingsEntry.TABLE_NAME}"

    object AppSettingsEntry : BaseColumns {
        const val TABLE_NAME = "app_settings"
        const val COLUMN_NAME_UUID = "uuid"
    }
}