package com.example.blooddonation.feature.admin

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import android.app.Activity
import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import coil.compose.rememberAsyncImagePainter
import com.example.blooddonation.domain.AdminBloodCamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.blooddonation.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


// Removed color aliases; use theme colors directly

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    viewModel: AdminViewModel = viewModel()
) {
    val camps = viewModel.camps
    var searchQuery by remember { mutableStateOf("") }
    var sortAsc by remember { mutableStateOf(true) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedCamp by remember { mutableStateOf<AdminBloodCamp?>(null) }
    val context = LocalContext.current

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Filter logic for both name and location
    val shownCamps = camps
        .filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.location.contains(searchQuery, ignoreCase = true)
        }
        .let { list ->
            if (sortAsc) list.sortedBy { it.date } else list.sortedByDescending { it.date }
        }

    // Scroll to top when sortAsc or searchQuery changes
    LaunchedEffect(sortAsc, searchQuery) {
        if (shownCamps.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Admin Dashboard") },
                navigationIcon = {
                    IconButton(onClick = {
                        // Fix for back button: if cannot pop, finish activity
                        if (!navController.popBackStack()) {
                            (context as? Activity)?.finish()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_logout),
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                selectedCamp = null
                showDialog = true
            }) { Icon(Icons.Default.Add, contentDescription = "Add Camp") }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Search and Sort Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by camp or location") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = {
                    sortAsc = !sortAsc
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Sort by date"
                    )
                }
            }

            // Camp List
            LazyColumn(state = listState) {
                items(shownCamps, key = { it.id }) { camp ->
                    CampItem(
                        camp = camp,
                        onEdit = {
                            selectedCamp = it
                            showDialog = true
                        },
                        onDelete = { viewModel.deleteCamp(it.id) }
                    )
                }

                if (shownCamps.isEmpty()) {
                    item {
                        Text(
                            text = "No camps found for “$searchQuery”",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }

    // Add/Edit Camp Dialog
    if (showDialog) {
        CampDialog(
            initialCamp = selectedCamp,
            onDismiss = { showDialog = false },
            onSave = { camp ->
                if (camp.id.isEmpty()) viewModel.addCamp(camp)
                else viewModel.updateCamp(camp)
                showDialog = false
            }
        )
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirm Logout", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to logout?", color = MaterialTheme.colorScheme.onBackground) },
            confirmButton = {
                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("signin") {
                            popUpTo("splash") { inclusive = true }
                            launchSingleTop = true
                        }
                        showLogoutDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Logout", color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showLogoutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground)
                ) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        )
    }
}


@Composable
fun CampItem(
    camp: AdminBloodCamp,
    onEdit: (AdminBloodCamp) -> Unit,
    onDelete: (AdminBloodCamp) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        elevation = CardDefaults.cardElevation()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.Top
            ) {
                // Image on the left as a rounded image
                if (camp.imageUrl.isNotEmpty()) {
                    val imageFile = rememberSaveable { File(camp.imageUrl) }
                    if (imageFile.exists()) {
                        Image(
                            painter = rememberAsyncImagePainter(imageFile),
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                        )
                    }
                } else {
                    // Placeholder (optional)
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                // Details and buttons
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = camp.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(text = "Location: ${camp.location}", color = MaterialTheme.colorScheme.onBackground)
                    Text(text = "Date: ${camp.date}", color = MaterialTheme.colorScheme.onBackground)
                    Text(text = camp.description, color = MaterialTheme.colorScheme.onBackground, maxLines = 2)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Button(
                    onClick = { onEdit(camp) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Edit", color = MaterialTheme.colorScheme.onPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { onDelete(camp) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground)
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

@Composable
fun CampDialog(
    initialCamp: AdminBloodCamp?,
    onDismiss: () -> Unit,
    onSave: (AdminBloodCamp) -> Unit
) {
    var name by remember { mutableStateOf(initialCamp?.name ?: "") }
    var location by remember { mutableStateOf(initialCamp?.location ?: "") }
    var date by remember { mutableStateOf(initialCamp?.date ?: "") }
    var description by remember { mutableStateOf(initialCamp?.description ?: "") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var savedImagePath by remember { mutableStateOf(initialCamp?.imageUrl ?: "") }

    var showValidationError by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // --- Date Picker State ---
    val calendar = Calendar.getInstance()
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    var showDatePicker by remember { mutableStateOf(false) }

    // --- Location Suggestions (Mock Data) ---
    val allLocations = listOf(
        "Kolkata", "Bangalore", "Delhi", "Mumbai", "Chennai", "Hyderabad", "Pune", "Ahmedabad",
        "Salt Lake", "Behala", "Park Street", "Howrah", "Dum Dum", "Garia"
    )
    val filteredLocations = remember(location) {
        if (location.isBlank()) emptyList()
        else allLocations.filter { it.contains(location, ignoreCase = true) }
    }
    var showSuggestions by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            scope.launch {
                val path = saveImageToInternalStorage(context, it)
                if (path != null) {
                    savedImagePath = path
                }
            }
        }
    }

    val isNameValid = name.isNotBlank()
    val isLocationValid = location.isNotBlank()
    val isDateValid = date.isNotBlank()
    val isFormValid = isNameValid && isLocationValid && isDateValid

    // DatePickerDialog launch logic
    if (showDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val pickedCalendar = Calendar.getInstance()
                pickedCalendar.set(year, month, dayOfMonth)
                date = dateFormat.format(pickedCalendar.time)
                showDatePicker = false
            },
            // Initial date selection
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setOnDismissListener { showDatePicker = false }
        }.show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.onPrimary,
        title = {
            Text(
                text = if (initialCamp == null) "Add Camp" else "Edit Camp",
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column {
                // Name field
                TextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (showValidationError) showValidationError = false
                    },
                    label = { Text("Camp Name") },
                    isError = showValidationError && !isNameValid,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        errorIndicatorColor = Color.Red
                    ),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                    singleLine = true
                )
                if (showValidationError && !isNameValid) {
                    Text(
                        "Camp name is required",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Location field with suggestions
                Box {
                    TextField(
                        value = location,
                        onValueChange = {
                            location = it
                            showSuggestions = it.isNotBlank() && filteredLocations.isNotEmpty()
                            if (showValidationError) showValidationError = false
                        },
                        label = { Text("Location") },
                        isError = showValidationError && !isLocationValid,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                            unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            errorIndicatorColor = Color.Red
                        ),
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                showSuggestions =
                                    focusState.isFocused && filteredLocations.isNotEmpty()
                            }
                    )
                    if (showSuggestions && filteredLocations.isNotEmpty()) {
                        DropdownMenu(
                            expanded = showSuggestions,
                            onDismissRequest = { showSuggestions = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                        ) {
                            filteredLocations.forEach { suggestion ->
                                DropdownMenuItem(
                                    text = { Text(suggestion) },
                                    onClick = {
                                        location = suggestion
                                        showSuggestions = false
                                    }
                                )
                            }
                        }
                    }
                }
                if (showValidationError && !isLocationValid) {
                    Text(
                        "Location is required",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Date picker field (read-only)
                TextField(
                    value = date,
                    onValueChange = {},
                    label = { Text("Date") },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_calendar), // Use your calendar icon here
                            contentDescription = "Pick Date",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { showDatePicker = true }
                        )
                    },
                    readOnly = true,
                    enabled = true,
                    isError = showValidationError && !isDateValid,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        errorIndicatorColor = Color.Red
                    ),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                )
                if (showValidationError && !isDateValid) {
                    Text(
                        "Date is required",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Description field
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { launcher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Choose Image", color = MaterialTheme.colorScheme.onPrimary)
                }

                if (savedImagePath.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        painter = rememberAsyncImagePainter(File(savedImagePath)),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(MaterialTheme.shapes.medium)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isFormValid) {
                        val camp = AdminBloodCamp(
                            id = initialCamp?.id ?: "",
                            name = name,
                            location = location,
                            date = date,
                            description = description,
                            imageUrl = savedImagePath
                        )
                        onSave(camp)
                    } else {
                        showValidationError = true
                    }
                },
                enabled = isFormValid,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Save", color = MaterialTheme.colorScheme.onPrimary)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground)
            ) {
                Text("Cancel", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    )
}

suspend fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    return withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = "camp_image_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}


