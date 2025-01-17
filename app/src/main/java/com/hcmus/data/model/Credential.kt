package com.hcmus.data.model

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "Credential")
class Credential() {
    @PrimaryKey
    @ColumnInfo(name = "email")
    var email: String = ""

    @ColumnInfo(name = "token")
    var token: String = ""
}


@Database(entities = [Credential::class], version = 1)
abstract class CredentialDatabase : RoomDatabase() {
    abstract fun credentialRepository(): CredentialDAO

    companion object {
        const val DB_NAME = "ngoitruongyeudau"
        private var instance: CredentialDatabase? = null

        fun getInstance(context: Context): CredentialDatabase {
            return instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, CredentialDatabase::class.java, DB_NAME).allowMainThreadQueries()
                .build()
    }
}

@Dao
interface CredentialDAO {
    @Query("SELECT * FROM Credential LIMIT 1")
    fun get(): Credential?

    @Query("DELETE FROM Credential")
    fun delete()

    @Insert
    fun insert(credential: Credential)
}