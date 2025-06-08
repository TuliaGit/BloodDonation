package com.example.blooddonation.feature.requestblood

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.blooddonation.feature.chat.generateChatId
import com.example.blooddonation.domain.Acceptance
import com.example.blooddonation.domain.BloodRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BloodRequestViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _requests = MutableStateFlow<List<BloodRequest>>(emptyList())
    val requests: StateFlow<List<BloodRequest>> = _requests

    init {
        loadRequests()
    }

    fun addRequest(request: BloodRequest, onComplete: (Boolean) -> Unit) {
        db.collection("bloodRequests")
            .add(request)
            .addOnSuccessListener {
                loadRequests()
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    private fun loadRequests() {
        db.collection("bloodRequests")
            .get()
            .addOnSuccessListener { result ->
                val list = result.documents.mapNotNull {
                    it.toObject(BloodRequest::class.java)?.copy(id = it.id)
                }
                _requests.value = list
            }
            .addOnFailureListener { e ->
                Log.e("BloodRequestViewModel", "Failed to load requests", e)
            }
    }

    fun rejectRequest(requestId: String) {
        db.collection("bloodRequests").document(requestId)
            .update("status", "rejected")
            .addOnSuccessListener {
                loadRequests()
            }
            .addOnFailureListener { e ->
                Log.e("BloodRequestViewModel", "Failed to reject request", e)
            }
    }

    fun getAcceptedRequestForDonor(donorId: String): BloodRequest? {
        return _requests.value.find {
            it.acceptedBy == donorId && it.status == "accepted"
        }
    }


    fun acceptRequest(
        requestId: String,
        donorId: String,
        medicalInfo: String,
        onChatReady: (chatId: String, requesterId: String) -> Unit
    ) {
        db.collection("bloodRequests").document(requestId).get()
            .addOnSuccessListener { doc ->
                val requesterId = doc.getString("requesterId") ?: return@addOnSuccessListener
                val chatId = generateChatId(donorId, requesterId)

                // 1️⃣ store donor’s medical info (optional)
                db.collection("acceptances").add(
                    Acceptance(requestId, donorId, medicalInfo)
                )

                // 2️⃣ ensure chat document exists
                val chatData = mapOf(
                    "donorId" to donorId,
                    "requesterId" to requesterId,
                    "createdAt" to FieldValue.serverTimestamp()
                )
                db.collection("chats").document(chatId).set(chatData, SetOptions.merge())

                // 3️⃣ update the blood-request with every piece the requester will need
                val updateMap = mapOf(
                    "status" to "accepted",
                    "acceptedBy" to donorId,
                    "chatId" to chatId          // 🔹 the key field
                )
                db.collection("bloodRequests").document(requestId)
                    .update(updateMap)
                    .addOnSuccessListener {
                        loadRequests()
                        onChatReady(chatId, requesterId)   // donor navigates now
                    }
            }
            .addOnFailureListener { e ->
                Log.e("BloodRequestViewModel", "Error in acceptRequest", e)
            }
    }
}
