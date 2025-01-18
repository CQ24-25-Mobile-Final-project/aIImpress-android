package com.hcmus.data

import androidx.room.TypeConverter
import com.hcmus.data.model.Gender

class GenderTypeConverter {
    @TypeConverter
    fun fromGender(gender: Gender): String {
        return gender.name
    }

    @TypeConverter
    fun toGender(gender: String): Gender {
        return Gender.valueOf(gender)
    }
}