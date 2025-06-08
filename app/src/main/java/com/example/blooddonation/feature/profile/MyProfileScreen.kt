package com.example.blooddonation.feature.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

import coil.compose.rememberAsyncImagePainter
import java.io.File
import androidx.lifecycle.ViewModelProvider
import coil.request.ImageRequest
import com.example.blooddonation.feature.theme.ThemeSwitch
import com.example.blooddonation.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileScreen(
    onBack: () -> Unit,
    uid: String,
    viewModel: ProfileViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                ProfileViewModel(uid) as T
        })
) {

    val profile by viewModel.profile.collectAsState()
    val ctx = LocalContext.current

    var editMode by remember { mutableStateOf(false) }
    var tmpName by remember { mutableStateOf("") }
    var tmpBio by remember { mutableStateOf("") }
    var tmpGroup by remember { mutableStateOf("") }
    var pickedImage by remember { mutableStateOf<Uri?>(null) }

    val pickImg = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        pickedImage = it
    }

    if (profile == null) {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }
    val user = profile!!

    @Composable
    fun avatarPainter(): Painter {
        pickedImage?.let { return rememberAsyncImagePainter(it) }
        val f = File(user.profileImagePath)
        return rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current)
                .data(if (f.exists()) f else R.drawable.ic_launcher_foreground)
                .build()
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
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
    ) { pad ->
        if (!editMode) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(pad)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.blood_background),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Black.copy(alpha = 0.45f))
                    )
                    Box(
                        modifier = Modifier
                            .size(156.dp)
                            .align(Alignment.BottomCenter)
                            .offset(y = 78.dp)
                            .clip(CircleShape)
                            .border(4.dp, MaterialTheme.colorScheme.onPrimaryContainer, CircleShape)
                            .shadow(12.dp, CircleShape)
                    ) {
                        Image(
                            painter = avatarPainter(),
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
                            modifier = Modifier.matchParentSize()
                        )
                    }
                }

                Spacer(Modifier.height(90.dp))

                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        user.username,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "Blood Group",
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        user.bloodGroup,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.SemiBold
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(24.dp))

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                alpha = 0.1f
                            )
                        ),
                        elevation = CardDefaults.cardElevation(0.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            user.bio.ifBlank { "No bio added yet." },
                            modifier = Modifier.padding(20.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onPrimaryContainer)
                        )
                    }

                    Spacer(Modifier.height(40.dp))

                    Button(
                        onClick = {
                            tmpName = user.username
                            tmpBio = user.bio
                            tmpGroup = user.bloodGroup
                            editMode = true
                        },
                        shape = RoundedCornerShape(32.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                    ) { Text("Edit Profile", color = MaterialTheme.colorScheme.onPrimary) }
                }
            }
        } else {
            /* ---------------- EDIT MODE ---------------- */
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(pad)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .clickable { pickImg.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = avatarPainter(),
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        modifier = Modifier.matchParentSize()
                    )
                    Box(
                        Modifier
                            .matchParentSize()
                            .background(Color.Black.copy(alpha = 0.35f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(Modifier.height(28.dp))

                OutlinedTextField(
                    value = tmpName,
                    onValueChange = { tmpName = it },
                    label = { Text("Username") },
                    singleLine = true,
                )

                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = tmpBio,
                    onValueChange = { tmpBio = it },
                    label = { Text("Bio") },
                    maxLines = 4,
                    modifier = Modifier
                        .heightIn(min = 120.dp),
                )

                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = tmpGroup,
                    onValueChange = { tmpGroup = it },
                    label = { Text("Blood Group") },
                    singleLine = true,
                )

                Spacer(Modifier.height(40.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.updateProfile(
                                user.copy(username = tmpName, bio = tmpBio, bloodGroup = tmpGroup),
                                pickedImage,
                                ctx
                            )
                            pickedImage = null
                            editMode = false
                        },
                        shape = RoundedCornerShape(32.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                    ) { Text("Save") }

                    OutlinedButton(
                        onClick = { editMode = false; pickedImage = null },
                        shape = RoundedCornerShape(32.dp),
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onPrimaryContainer),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                    ) { Text("Cancel", color = MaterialTheme.colorScheme.onPrimaryContainer) }
                }
            }
        }
    }
}
