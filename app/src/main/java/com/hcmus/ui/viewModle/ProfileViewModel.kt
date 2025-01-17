package com.hcmus.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.hcmus.data.MediaFileDatabase
import com.hcmus.data.ProfileRepository
import com.hcmus.data.model.Gender
import com.hcmus.data.model.Profile
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ProfileRepository
    val allProfiles: LiveData<List<Profile>>

    init {
        val profileDao = MediaFileDatabase.getDatabase(application).profileDao()
        repository = ProfileRepository(profileDao)
        allProfiles = repository.getAllProfiles()
    }

    fun insert(profile: Profile) = viewModelScope.launch {
        val existingProfile = repository.getProfileByEmail(profile.email)
        if (existingProfile != null) {
            // Update the existing profile
            update(
                email = profile.email,
                fullName = profile.fullName,
                phoneNumber = profile.phoneNumber,
                country = profile.country,
                gender = profile.gender,
                avatarUrl = profile.avatarUrl
            )
        } else {
            repository.insert(profile)
        }
    }

    fun update(email: String, fullName: String, phoneNumber: String, country: String, gender: Gender, avatarUrl: String?
    ) = viewModelScope.launch {
        repository.update( email = email, fullName = fullName, phoneNumber = phoneNumber, country = country, gender = gender, avatarUrl = avatarUrl )
    }

    fun delete(profile: Profile) = viewModelScope.launch {
        repository.delete(profile)
    }

    suspend fun getProfileById(id: Int): Profile? {
        return repository.getProfileById(id)
    }

    suspend fun getProfileByEmail(email: String): Profile? {
        return repository.getProfileByEmail(email)
    }
}