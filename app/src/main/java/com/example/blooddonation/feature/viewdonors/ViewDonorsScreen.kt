package com.example.blooddonation.feature.viewdonors

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.blooddonation.domain.BloodRequest
import com.example.blooddonation.feature.requestblood.BloodRequestViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.blooddonation.feature.theme.ThemeSwitch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewDonorsScreen(
    onNavigateToChat: (chatId: String, currentUserId: String, requesterId: String) -> Unit,
    onBack: () -> Unit,
    viewModel: BloodRequestViewModel = viewModel(),
    currentUserId: String
) {
    val requests by viewModel.requests.collectAsState()
    var showMedicalFormForRequest by remember { mutableStateOf<BloodRequest?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabTitles = listOf("Pending", "Accepted", "Rejected")

    // --- Tab filtering logic ---
    val pendingRequests = requests.filter { it.status == "pending" }
    val acceptedRequests = requests.filter { it.status == "accepted" }
    val rejectedRequests = requests.filter { it.status == "rejected" }

    // Show medical dialog if needed
    showMedicalFormForRequest?.let { request ->
        MedicalFormDialog(
            request = request,
            donorId = currentUserId,
            onDismiss = { showMedicalFormForRequest = null },
            onSubmit = { requestId, donorId, medicalInfo ->
                viewModel.acceptRequest(requestId, donorId, medicalInfo) { chatId, requesterId ->
                    showMedicalFormForRequest = null
                    onNavigateToChat(chatId, currentUserId, requesterId)
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        topBar = {
            TopAppBar(
                title = { Text("View Requests") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    ThemeSwitch()
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(8.dp)
        ) {
            // ---- Tab Bar ----
            androidx.compose.material3.TabRow(
                selectedTabIndex = selectedTab
            ) {
                tabTitles.forEachIndexed { index, title ->
                    androidx.compose.material3.Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Tab bar logic (unchanged)
            when (selectedTab) {
                0 -> PendingTab(
                    requests = pendingRequests,
                    currentUserId = currentUserId,
                    onAccept = { showMedicalFormForRequest = it },
                    onReject = { viewModel.rejectRequest(it.id) }
                )
                1 -> AcceptedTab(
                    requests = acceptedRequests,
                    currentUserId = currentUserId,
                    onNavigateToChat = onNavigateToChat
                )
                2 -> RejectedTab(
                    requests = rejectedRequests,
                    currentUserId = currentUserId
                )
            }

        }
    }
}

@Composable
private fun PendingTab(
    requests: List<BloodRequest>,
    currentUserId: String,
    onAccept: (BloodRequest) -> Unit,
    onReject: (BloodRequest) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(requests, key = { it.id }) { request ->
            RequestCard(
                bloodRequest = request,
                currentUserId = currentUserId,
                onAccept = { onAccept(request) },
                onReject = { onReject(request) }
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun AcceptedTab(
    requests: List<BloodRequest>,
    currentUserId: String,
    onNavigateToChat: (chatId: String, currentUserId: String, requesterId: String) -> Unit
) {
    if (requests.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No accepted requests.")
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(requests, key = { it.id }) { request ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Blood Group: ${request.bloodGroup}", fontWeight = FontWeight.Bold)
                        Text("Location: ${request.location}")
                        if (request.requesterId == currentUserId) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Raised by you",
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.08f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        if (!request.chatId.isNullOrEmpty() && request.requesterId != currentUserId) {
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    onNavigateToChat(
                                        request.chatId,
                                        currentUserId,
                                        request.requesterId
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                            ) {
                                Text("Go to Chat", color = MaterialTheme.colorScheme.onTertiaryContainer)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun RejectedTab(requests: List<BloodRequest>, currentUserId: String) {
    if (requests.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No rejected requests.")
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(requests, key = { it.id }) { request ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Blood Group: ${request.bloodGroup}", fontWeight = FontWeight.Bold)
                        Text("Location: ${request.location}")
                        if (request.requesterId == currentUserId) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Raised by you",
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.08f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}


@Composable
fun RequestCard(
    bloodRequest: BloodRequest,
    currentUserId: String,
    onAccept: (() -> Unit)? = null, // Optional
    onReject: (() -> Unit)? = null  // Optional
) {
    val isRaisedByMe = bloodRequest.requesterId == currentUserId

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "Blood Group: ${bloodRequest.bloodGroup}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = "Location: ${bloodRequest.location}")

            if (isRaisedByMe) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Raised by you",
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            if (!isRaisedByMe && onAccept != null && onReject != null) {
                Row {
                    Button(
                        onClick = onAccept,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                    ) { Text("Accept") }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = onReject,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) { Text("Reject") }
                }
            }
        }
    }
}


@Composable
fun MedicalFormDialog(
    request: BloodRequest,
    donorId: String,
    onDismiss: () -> Unit,
    onSubmit: (requestId: String, donorId: String, medicalInfo: String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            tonalElevation = 4.dp,
            modifier = Modifier.wrapContentHeight()
        ) {
            val scrollState = rememberScrollState()
            var age by remember { mutableStateOf("") }
            var weight by remember { mutableStateOf("") }
            var hadIllness by remember { mutableStateOf("") }
            var medications by remember { mutableStateOf("") }
            var surgery by remember { mutableStateOf("") }
            var alcohol by remember { mutableStateOf("") }
            var chronicDiseases by remember { mutableStateOf("") }
            var exposedCovid by remember { mutableStateOf("") }

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    "Eligibility Form",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it.filter(Char::isDigit) },
                    label = { Text("Your age") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it.filter(Char::isDigit) },
                    label = { Text("Current weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                YesNoDropdown(
                    label = "Had illness in the last 7 days?",
                    selected = hadIllness,
                    onSelect = { hadIllness = it }
                )
                Spacer(Modifier.height(8.dp))

                YesNoDropdown(
                    label = "Any medications you are taking?",
                    selected = medications,
                    onSelect = { medications = it }
                )
                Spacer(Modifier.height(8.dp))

                YesNoDropdown(
                    label = "Surgery in past 6 months?",
                    selected = surgery,
                    onSelect = { surgery = it }
                )
                Spacer(Modifier.height(8.dp))

                YesNoDropdown(
                    label = "Alcohol in last 24 hours?",
                    selected = alcohol,
                    onSelect = { alcohol = it }
                )
                Spacer(Modifier.height(8.dp))

                YesNoDropdown(
                    label = "Any chronic diseases?",
                    selected = chronicDiseases,
                    onSelect = { chronicDiseases = it }
                )
                Spacer(Modifier.height(8.dp))

                YesNoDropdown(
                    label = "Exposed to COVID-19 recently?",
                    selected = exposedCovid,
                    onSelect = { exposedCovid = it }
                )
                Spacer(Modifier.height(24.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(Modifier.width(12.dp))
                    Button(
                        enabled = listOf(
                            age, weight, hadIllness, medications,
                            surgery, alcohol, chronicDiseases, exposedCovid
                        ).all { it.isNotBlank() },
                        onClick = {
                            // pack answers into a single string; adjust as you like
                            val info = buildString {
                                append("age=$age; ")
                                append("weight=$weight; ")
                                append("illness=$hadIllness; ")
                                append("medications=$medications; ")
                                append("surgery=$surgery; ")
                                append("alcohol=$alcohol; ")
                                append("chronicDiseases=$chronicDiseases; ")
                                append("exposedCovid=$exposedCovid")
                            }
                            onSubmit(request.id, donorId, info)
                        }
                    ) { Text("Submit") }
                }
            }
        }
    }
}

@Composable
private fun YesNoDropdown(
    label: String,
    selected: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selected,
            onValueChange = { /* read-only */ },
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            listOf("Yes", "No").forEach { answer ->
                DropdownMenuItem(
                    text = { Text(answer) },
                    onClick = {
                        onSelect(answer)
                        expanded = false
                    }
                )
            }
        }
    }
}
