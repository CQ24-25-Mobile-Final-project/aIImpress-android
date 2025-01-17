package com.hcmus.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hcmus.ui.display.MediaFile

@Database(entities = [MediaFile::class], version = 1)
@TypeConverters(UriTypeConverter::class)
abstract class MediaFileDatabase : RoomDatabase() {
    abstract fun mediaFileDao(): MediaFileDao

    companion object {
        @Volatile
        private var INSTANCE: MediaFileDatabase? = null

        fun getDatabase(context: Context): MediaFileDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MediaFileDatabase::class.java,
                    "media_file_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}