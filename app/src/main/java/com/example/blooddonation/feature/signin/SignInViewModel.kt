package com.example.blooddonation.feature.signin

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SignInViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun signIn(
        email: String,
        password: String,
        onResult: (uid: String, isAdmin: Boolean, profileExists: Boolean) -> Unit
    ) {
        _isLoading.value = true
        _errorMessage.value = null

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid.orEmpty()
                firestore.collection("users").document(uid).get()
                    .addOnSuccessListener { doc ->
                        _isLoading.value = false
                        if (doc.exists()) {
                            val role = doc.getString("role") ?: ""
                            val isAdmin = role == "Admin"
                            onResult(uid, isAdmin, true)
                        } else {
                            onResult(uid, false, false)
                        }
                    }
                    .addOnFailureListener { e ->
                        _errorMessage.value = "Failed to load profile: ${e.message}"
                        _isLoading.value = false
                    }
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Authentication failed: ${e.message}"
                _isLoading.value = false
            }
    }
}
