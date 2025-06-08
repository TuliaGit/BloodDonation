package com.example.blooddonation.feature.dashboard

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.blooddonation.R
import com.example.blooddonation.feature.theme.ThemeSwitch
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    uid: String,
    onAboutUs: () -> Unit,
    onOurWork: () -> Unit,
    onHelp: () -> Unit,
    onLogout: () -> Unit,
    onViewDonors: () -> Unit,
    onRequestBlood: () -> Unit,
    onMyProfile: () -> Unit,
    onBloodCampList: () -> Unit,
    viewModel: DashboardViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val colorScheme = MaterialTheme.colorScheme

    var showBotDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val botAnswers = mapOf(
        "how to request blood?" to "Go to the Dashboard and tap 'Request Blood'. Fill the form and submit.",
        "how to become a donor?" to "Create a profile and enable 'Donor Mode' in settings.",
        "how to update profile?" to "Tap 'My Profile' from Dashboard and click 'Edit'.",
        "what is crimsonsync?" to "CrimsonSync is a blood bank app for connecting donors and recipients.",
        "how to view donors?" to "Tap on 'View Donors' on the Dashboard to see a list of donors.",
        "how to contact a donor?" to "Request blood first. If accepted, you can chat with the donor.",
        "is crimsonsync free?" to "Yes! CrimsonSync is completely free to use.",
        "how to logout?" to "Open the menu and select 'Logout' at the bottom."
    )

    LaunchedEffect(uid) {
        viewModel.loadUser(uid)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "CrimsonSync",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("About Us") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onAboutUs()
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Our Work") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onOurWork()
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Help") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onHelp()
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Logout") },
                    selected = false,
                    onClick = {
                        showLogoutDialog = true
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            topBar = {
                TopAppBar(
                    title = { Text("Dashboard", color = colorScheme.onPrimary) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = colorScheme.onPrimary
                            )
                        }
                    },
                    actions = {
                        ThemeSwitch()
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.primary)
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showBotDialog = true },
                    containerColor = colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = "Chatbot",
                        tint = colorScheme.onPrimary
                    )
                }
            },
            content = { innerPadding ->
                if (showLogoutDialog) {
                    AlertDialog(
                        onDismissRequest = { showLogoutDialog = false },
                        title = {
                            Text(
                                "Confirm Logout",
                                color = colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        text = {
                            Text(
                                "Are you sure you want to logout?",
                                color = colorScheme.onSurface
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    FirebaseAuth.getInstance().signOut()
                                    showLogoutDialog = false
                                    onLogout()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
                            ) {
                                Text("Logout", color = colorScheme.onPrimary)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showLogoutDialog = false }) {
                                Text("Cancel", color = colorScheme.primary)
                            }
                        }
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = colorScheme.primary
                        )
                    } else if (uiState.errorMessage != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = uiState.errorMessage ?: "An error occurred",
                                color = colorScheme.primary,
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(onClick = onLogout) {
                                Text("Go to Sign In")
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape)
                                        .background(colorScheme.surface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!uiState.imageUri.isNullOrEmpty()) {
                                        val imageFile = File(uiState.imageUri!!)
                                        if (imageFile.exists()) {
                                            AsyncImage(
                                                model = Uri.fromFile(imageFile),
                                                contentDescription = "Profile Picture",
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(CircleShape),
                                                contentScale = ContentScale.Crop,
                                                error = painterResource(id = android.R.drawable.ic_menu_gallery)
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = "Default Profile Picture",
                                                modifier = Modifier.size(50.dp),
                                                tint = colorScheme.onSurface
                                            )
                                        }
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "Default Profile Picture",
                                            modifier = Modifier.size(50.dp),
                                            tint = colorScheme.onSurface
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        text = "Welcome\n${uiState.username}",
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            color = colorScheme.onBackground
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(48.dp))

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                item {
                                    DashboardCard(
                                        title = "View Donors",
                                        icon = Icons.Default.Face,
                                        onClick = onViewDonors
                                    )
                                }
                                item {
                                    DashboardCard(
                                        title = "Request Blood",
                                        icon = Icons.Default.Favorite,
                                        onClick = onRequestBlood
                                    )
                                }
                                item {
                                    DashboardCard(
                                        title = "My Profile",
                                        icon = Icons.Default.Person,
                                        onClick = onMyProfile
                                    )
                                }
                                item {
                                    DashboardCard(
                                        title = "Events",
                                        icon = Icons.Default.Search,
                                        onClick = onBloodCampList
                                    )
                                }
                            }
                        }
                    }
                }

                if (showBotDialog) {
                    AlertDialog(
                        onDismissRequest = { showBotDialog = false },
                        confirmButton = {
                            TextButton(onClick = { showBotDialog = false }) {
                                Text("Close", color = colorScheme.primary)
                            }
                        },
                        title = { Text("CrimsonBot - FAQs", color = colorScheme.primary) },
                        text = {
                            Column {
                                var userQuestion by remember { mutableStateOf("") }
                                var botReply by remember { mutableStateOf("") }

                                OutlinedTextField(
                                    value = userQuestion,
                                    onValueChange = {
                                        userQuestion = it
                                    },
                                    label = { Text("Ask a question") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        val cleanedInput = userQuestion.trim().lowercase()
                                        botReply =
                                            botAnswers.entries.firstOrNull { it.key.lowercase() == cleanedInput }?.value
                                                ?: "Sorry, I didn't understand that. Try asking a different question."
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
                                ) {
                                    Text("Send", color = colorScheme.onPrimary)
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                if (botReply.isNotEmpty()) {
                                    Text(
                                        text = botReply,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = colorScheme.onBackground
                                    )
                                }

                            }
                        }
                    )
                }
            }
        )
    }
}

// ---------- reusable template ----------
@Composable
private fun StaticInfoScreen(
    @DrawableRes hero: Int,
    title: String,
    body: String
) {
    val colorScheme = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(colorScheme.primary, colorScheme.onBackground),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Image(
                    painter = painterResource(id = hero),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(
                    modifier = Modifier
                        .height(4.dp)
                        .fillMaxWidth()
                        .background(
                            colorScheme.primary,
                            RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
                        )
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = colorScheme.onBackground,
                        lineHeight = 22.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun AboutUsScreen() = StaticInfoScreen(
    hero = R.drawable.aboutus,
    title = "About CrimsonSync",
    body = "CrimsonSync is a community-powered platform dedicated to connecting blood donors " +
            "with recipients quickly and efficiently. Our mission is to save lives by building " +
            "a trustworthy and fast blood-donation ecosystem."
)

@Composable
fun OurWorkScreen() = StaticInfoScreen(
    hero = R.drawable.ourwork,
    title = "What Drives Us",
    body = "“Every drop counts. We connect hearts to help save lives.”\n\n" +
            "CrimsonSync works to make blood donation seamless, reliable, and community-driven."
)

@Composable
fun HelpScreen() = StaticInfoScreen(
    hero = R.drawable.helpus,
    title = "Need Help?",
    body = "For any queries or assistance, reach out to us at:\n\n" +
            "crimsonsync@gmail.com\n\n" +
            "Our team is here to support you 24 × 7."
)

@Composable
fun DashboardCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .height(140.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = colorScheme.onPrimary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                color = colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
