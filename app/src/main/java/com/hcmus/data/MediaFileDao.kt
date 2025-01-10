package com.hcmus.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import com.hcmus.ui.display.MediaFile

class MediaFileDao(context: Context) {
    private val dbHelper = MediaFileDatabaseHelper(context)

    fun insertMediaFile(mediaFile: MediaFile) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(MediaFileDatabaseHelper.COLUMN_URI, mediaFile.uri.toString())
            put(MediaFileDatabaseHelper.COLUMN_TAG, mediaFile.tag)
            put(MediaFileDatabaseHelper.COLUMN_NAME, mediaFile.name)
            put(MediaFileDatabaseHelper.COLUMN_DATE_ADDED, mediaFile.dateAdded)
            put(MediaFileDatabaseHelper.COLUMN_URL, mediaFile.url?.toString())
        }
        db.insert(MediaFileDatabaseHelper.TABLE_MEDIA_FILES, null, values)
    }

    fun updateMediaFile(mediaFile: MediaFile) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(MediaFileDatabaseHelper.COLUMN_TAG, mediaFile.tag)
            put(MediaFileDatabaseHelper.COLUMN_NAME, mediaFile.name)
            put(MediaFileDatabaseHelper.COLUMN_DATE_ADDED, mediaFile.dateAdded)
            put(MediaFileDatabaseHelper.COLUMN_URL, mediaFile.url?.toString())
        }
        db.update(MediaFileDatabaseHelper.TABLE_MEDIA_FILES, values, "${MediaFileDatabaseHelper.COLUMN_URI} = ?", arrayOf(mediaFile.uri.toString()))
    }

    fun deleteMediaFile(uri: Uri) {
        val db = dbHelper.writableDatabase
        db.delete(MediaFileDatabaseHelper.TABLE_MEDIA_FILES, "${MediaFileDatabaseHelper.COLUMN_URI} = ?", arrayOf(uri.toString()))
    }

    fun getAllMediaFiles(): List<MediaFile> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(MediaFileDatabaseHelper.TABLE_MEDIA_FILES, null, null, null, null, null, null)
        return cursorToList(cursor)
    }

    private fun cursorToList(cursor: Cursor): List<MediaFile> {
        val mediaFiles = mutableListOf<MediaFile>()
        with(cursor) {
            while (moveToNext()) {
                val uri = Uri.parse(getString(getColumnIndexOrThrow(MediaFileDatabaseHelper.COLUMN_URI)))
                val tag = getString(getColumnIndexOrThrow(MediaFileDatabaseHelper.COLUMN_TAG))
                val name = getString(getColumnIndexOrThrow(MediaFileDatabaseHelper.COLUMN_NAME))
                val dateAdded = getLong(getColumnIndexOrThrow(MediaFileDatabaseHelper.COLUMN_DATE_ADDED))
                val url = getString(getColumnIndexOrThrow(MediaFileDatabaseHelper.COLUMN_URL))?.let { Uri.parse(it) }
                mediaFiles.add(MediaFile(uri, tag, name, dateAdded, url))
            }
        }
        cursor.close()
        return mediaFiles
    }
}