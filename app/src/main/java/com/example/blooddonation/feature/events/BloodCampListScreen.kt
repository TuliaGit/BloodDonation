package com.example.blooddonation.feature.events

import BloodCampViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.blooddonation.domain.BloodCamp

@Composable
fun BloodCampListScreen(viewModel: BloodCampViewModel = viewModel()) {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        // Search bar and sort button Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search text field
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        "Search by location",
                        color = Color.LightGray
                    )
                }, // Set placeholder text color here
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.DarkGray,
                    unfocusedContainerColor = Color.DarkGray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedPlaceholderColor = Color.LightGray,
                    unfocusedPlaceholderColor = Color.LightGray
                ),

                singleLine = true,
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = Color.White
                            )
                        }
                    }
                }
            )


            Spacer(modifier = Modifier.width(8.dp))

            // Sort button
            IconButton(
                onClick = { isSortAscending = !isSortAscending },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (isSortAscending) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropDown,
                    contentDescription = "Sort by date",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // LazyColumn of filtered camps
        LazyColumn {
            items(filteredCamps) { camp ->
                BloodCampItem(
                    modifier = Modifier.fillMaxWidth(),
                    camp = camp,
                    isRegistered = registeredCampIds.contains(camp.id),
                    onRegisterClick = { viewModel.registerForCamp(camp.id) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (filteredCamps.isEmpty()) {
                item {
                    Text(
                        "No camps found for \"$searchQuery\"",
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.LightGray,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


@Composable
fun BloodCampItem(
    modifier: Modifier = Modifier,
    camp: BloodCamp,
    isRegistered: Boolean,
    onRegisterClick: () -> Unit
) {

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        elevation = CardDefaults.cardElevation()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (camp.imageUrl.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(model = camp.imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(text = camp.name, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            Text(text = "Location: ${camp.location}", color = MaterialTheme.colorScheme.onBackground)
            Text(text = "Date: ${camp.date}", color = MaterialTheme.colorScheme.onBackground)
            Text(text = camp.description, color = MaterialTheme.colorScheme.onBackground)

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onRegisterClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRegistered) Color.Gray else MaterialTheme.colorScheme.primary
                ),
                enabled = !isRegistered
            ) {
                Text(
                    text = if (isRegistered) "Registered" else "Register",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}



