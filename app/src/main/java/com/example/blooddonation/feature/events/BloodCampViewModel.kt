import androidx.lifecycle.ViewModel
import com.example.blooddonation.domain.BloodCamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BloodCampViewModel : ViewModel() {
    private val db = Firebase.firestore

    private val _camps = MutableStateFlow<List<BloodCamp>>(emptyList())
    val camps: StateFlow<List<BloodCamp>> = _camps

    private val _registeredCampIds = MutableStateFlow<List<String>>(emptyList())
    val registeredCampIds: StateFlow<List<String>> = _registeredCampIds

    init {
        loadCamps()
    }

    private fun loadCamps() {
        db.collection("blood_camps")
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    val campList = mutableListOf<BloodCamp>()
                    for (doc in it.documents) {
                        val camp = doc.toObject(BloodCamp::class.java)
                        camp?.let { c -> campList.add(c.copy(id = doc.id)) }
                    }
                    _camps.value = campList
                }
            }
    }

    fun registerForCamp(campId: String) {
        if (!_registeredCampIds.value.contains(campId)) {
            _registeredCampIds.value += campId
        }
    }
}
