package com.hcmus.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MediaFileDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_MEDIA_FILES (
                $COLUMN_URI TEXT PRIMARY KEY,
                $COLUMN_TAG TEXT,
                $COLUMN_NAME TEXT,
                $COLUMN_DATE_ADDED INTEGER,
                $COLUMN_URL TEXT
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MEDIA_FILES")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "media_files.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_MEDIA_FILES = "media_files"
        const val COLUMN_URI = "uri"
        const val COLUMN_TAG = "tag"
        const val COLUMN_NAME = "name"
        const val COLUMN_DATE_ADDED = "date_added"
        const val COLUMN_URL = "url"
    }
}