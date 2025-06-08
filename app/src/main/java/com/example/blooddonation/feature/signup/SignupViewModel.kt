package com.example.blooddonation.feature.signup

import androidx.lifecycle.ViewModel
import com.example.blooddonation.domain.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SignupViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isRegistered = MutableStateFlow(false)
    val isRegistered: StateFlow<Boolean> = _isRegistered

    private val _registrationError = MutableStateFlow<String?>(null)
    val registrationError: StateFlow<String?> = _registrationError

    private val _currentUid = MutableStateFlow<String?>(null)
    val currentUid: StateFlow<String?> = _currentUid

    fun registerUser(user: User, password: String) {
        _isLoading.value = true
        _registrationError.value = null
        _isRegistered.value = false

        auth.createUserWithEmailAndPassword(user.email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid.orEmpty()
                    _currentUid.value = uid
                    val newUser = user.copy(uid = uid)
                    firestore.collection("users")
                        .document(uid)
                        .set(newUser)
                        .addOnSuccessListener {
                            _isRegistered.value = true
                            _isLoading.value = false
                        }
                        .addOnFailureListener { e ->
                            _registrationError.value = "Failed to save user: ${e.message}"
                            _isLoading.value = false
                        }
                } else {
                    _registrationError.value =
                        task.exception?.localizedMessage ?: "Registration failed"
                    _isLoading.value = false
                }
            }
    }
}
