package com.example.blooddonation.feature.events

import BloodCampViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.blooddonation.feature.theme.ThemeSwitch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodCampListScreen(onBack: () -> Unit = {}, viewModel: BloodCampViewModel = viewModel()) {
    val camps by viewModel.camps.collectAsStateWithLifecycle()
    val registeredCampIds by viewModel.registeredCampIds.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var isSortAscending by remember { mutableStateOf(true) }

    // Filter camps by location search query (case-insensitive)
    val filteredCamps = camps.filter {
        it.location.contains(searchQuery, ignoreCase = true)
    }.let { list ->
        if (isSortAscending) list.sortedBy { it.date }
        else list.sortedByDescending { it.date }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        topBar = {
            TopAppBar(
                title = { Text("Blood Camps") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = { ThemeSwitch() },
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
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by location") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = { isSortAscending = !isSortAscending }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Sort by date")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(filteredCamps, key = { it.id }) { camp ->
                    CampCard(
                        modifier = Modifier.fillMaxWidth(),
                        name = camp.name,
                        location = camp.location,
                        date = camp.date,
                        description = camp.description,
                        imagePath = camp.imageUrl
                    ) {
                        Button(
                            onClick = { viewModel.registerForCamp(camp.id) },
                            enabled = !registeredCampIds.contains(camp.id),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (registeredCampIds.contains(camp.id)) Color.Gray else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = if (registeredCampIds.contains(camp.id)) "Registered" else "Register",
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (filteredCamps.isEmpty()) {
                    item {
                        Text(
                            "No camps found for \"$searchQuery\"",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

