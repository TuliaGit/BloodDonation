package com.example.blooddonation.feature

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth


    /**
     * TODO: MVVM Pattern
     *
     * data, domain, ui/presentation
     *
     * data -
     *
     * domain -
     *
     * ui -
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        setContent {

            /**
             * TODO: Tasks:
             * 1. Implement MVVM architecture for the project (https://developer.android.com/topic/architecture)
             * 2. ViewModels for Register, Profile, Dashboard
             * 3. Dependency injection using Hilt (https://developer.android.com/training/dependency-injection)
             */

            BloodBankApp(auth.currentUser)
        }
    }
}