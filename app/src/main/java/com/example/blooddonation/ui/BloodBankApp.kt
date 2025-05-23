package com.example.blooddonation.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.example.blooddonation.navigation.AppNavigation
import com.example.blooddonation.ui.theme.BloodBankTheme
import com.google.firebase.auth.FirebaseUser

@Composable
fun BloodBankApp(currentUser: FirebaseUser?) {
    BloodBankTheme {
        val navController = rememberNavController()

        LaunchedEffect(Unit) {
            if (currentUser != null) {
                val name = currentUser.displayName ?: "Default Name"
                val imageUri = currentUser.photoUrl?.toString() ?: "default_image_uri"

                // Logging the values before navigation
                Log.d("BloodBankApp", "Navigating to dashboard with name: $name, imageUri: $imageUri")
                if (name.isNotEmpty() && imageUri.isNotEmpty()) {
                    navController.navigate("dashboard/$name/$imageUri")
                } else {
                    Log.e("BloodBankApp", "Navigation arguments are invalid: name='$name', imageUri='$imageUri'")
                }
            }
        }

        AppNavigation(navController)
    }
}