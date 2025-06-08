package com.example.blooddonation.feature.profile

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class ProfileViewModel(private val uid: String) : ViewModel() {

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile

    init { fetchProfile() }

    private fun fetchProfile() {
        FirebaseFirestore.getInstance()
            .collection("users").document(uid)
            .addSnapshotListener { snap, error ->
                if (error != null) {
                    Log.e("ProfileVM", "Firestore error", error)
                    _profile.value = null
                } else if (snap != null && snap.exists()) {
                    _profile.value = snap.toObject(UserProfile::class.java) ?: UserProfile()
                } else {
                    Log.w("ProfileVM", "No profile doc for $uid – using empty stub")
                    _profile.value = UserProfile()      // show blank but editable profile
                }
            }
    }

    fun updateProfile(updated: UserProfile, newImageUri: Uri?, ctx: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val imagePath = newImageUri?.let { uri ->
                val file = File(ctx.cacheDir, "$uid.jpg")
                ctx.contentResolver.openInputStream(uri)?.use { it.copyTo(file.outputStream()) }
                file.absolutePath
            } ?: updated.profileImagePath

            FirebaseFirestore.getInstance()
                .collection("users").document(uid)
                .set(updated.copy(profileImagePath = imagePath))
        }
    }
}

