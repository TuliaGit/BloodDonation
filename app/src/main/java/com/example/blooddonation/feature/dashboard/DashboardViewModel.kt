package com.example.blooddonation.feature.dashboard

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class DashboardUiState(
    val username: String = "",
    val imageUri: String? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class DashboardViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState

    fun loadUser(uid: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    _uiState.value = DashboardUiState(
                        username = document.getString("username") ?: "",
                        imageUri = document.getString("profileImagePath"),
                        isLoading = false
                    )
                } else {
                    _uiState.value = DashboardUiState(
                        isLoading = false,
                        errorMessage = "User profile not found"
                    )
                }
            }
            .addOnFailureListener { e ->
                _uiState.value = DashboardUiState(
                    isLoading = false,
                    errorMessage = "Error loading profile: ${e.message}"
                )
            }
    }
}
