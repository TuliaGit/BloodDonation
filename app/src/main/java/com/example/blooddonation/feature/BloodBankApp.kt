package com.example.blooddonation.feature

import android.util.Log
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.blooddonation.navigation.AppNavigation
import com.example.blooddonation.navigation.Screen
import com.example.blooddonation.feature.theme.BloodBankTheme
import com.example.blooddonation.feature.theme.LocalIsDarkTheme
import com.example.blooddonation.feature.theme.LocalToggleTheme
import com.google.firebase.auth.FirebaseUser

@Composable
fun BloodBankApp(currentUser: FirebaseUser?) {
    var isDarkTheme by remember { mutableStateOf(false) }
    CompositionLocalProvider(
        LocalIsDarkTheme provides isDarkTheme,
        LocalToggleTheme provides { isDarkTheme = !isDarkTheme }
    ) {
        BloodBankTheme(darkTheme = isDarkTheme) {
            val navController = rememberNavController()

            LaunchedEffect(Unit) {
                if (currentUser != null) {
                    val name = currentUser.displayName ?: "Default Name"
                    val imageUri = currentUser.photoUrl?.toString() ?: "default_image_uri"

                    // Logging the values before navigation
                    Log.d("BloodBankApp", "Navigating to dashboard with name: $name, imageUri: $imageUri")
                    if (name.isNotEmpty() && imageUri.isNotEmpty()) {
                        val uid = currentUser.uid
                        val encodedUri = Uri.encode(imageUri)
                        navController.navigate(Screen.Dashboard.createRoute(name, encodedUri, uid))
                    } else {
                        Log.e("BloodBankApp", "Navigation arguments are invalid: name='$name', imageUri='$imageUri'")
                    }
                }
            }

            AppNavigation(navController)
        }
    }
}