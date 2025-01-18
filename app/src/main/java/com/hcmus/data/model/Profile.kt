package com.hcmus.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class Profile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val avatarUrl: String? = null,
    var fullName: String,    
    var email: String,       
    var phoneNumber: String, 
    var country: String,     
    var gender: Gender       
)

// Enum đại diện cho giới tính
enum class Gender {
    Male,
    Female,
    Other
}
