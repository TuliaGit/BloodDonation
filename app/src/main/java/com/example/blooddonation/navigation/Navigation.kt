package com.example.blooddonation.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.blooddonation.feature.chat.ChatScreen
import com.example.blooddonation.feature.admin.AdminDashboardScreen
import com.example.blooddonation.feature.admin.AdminViewModel
import com.example.blooddonation.feature.dashboard.AboutUsScreen
import com.example.blooddonation.feature.dashboard.DashboardScreen
import com.example.blooddonation.feature.dashboard.HelpScreen
import com.example.blooddonation.feature.dashboard.OurWorkScreen
import com.example.blooddonation.feature.events.BloodCampListScreen
import com.example.blooddonation.feature.profile.MyProfileScreen
import com.example.blooddonation.feature.profile.ProfileCreationScreen
import com.example.blooddonation.feature.signup.SignUpScreen
import com.example.blooddonation.feature.signup.SignupViewModel
import com.example.blooddonation.feature.requestblood.BloodRequestViewModel
import com.example.blooddonation.feature.splashscreen.SplashScreen
import com.example.blooddonation.feature.requestblood.BloodRequestScreen
import com.example.blooddonation.feature.signin.SignInScreen
import com.example.blooddonation.feature.viewdonors.ViewDonorsScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    val userViewModel: SignupViewModel = viewModel()
    val adminViewModel: AdminViewModel = viewModel()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(
                onNavigateToAdminDashboard = {
                    navController.navigate("admin_dashboard") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToDashboard = { uid ->
                    navController.navigate("dashboard/$uid") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToSignup = {
                    navController.navigate("signup") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("signup") {
            SignUpScreen(
                viewModel = userViewModel,
                onNavigateToProfile = { routeId ->
                    if (routeId == "admin_dashboard") {
                        navController.navigate("admin_dashboard") {
                            popUpTo("signup") { inclusive = true }
                        }
                    } else {
                        navController.navigate("profile/$routeId") {
                            popUpTo("signup") { inclusive = true }
                        }
                    }
                },
                onSignInClick = {
                    navController.navigate("signin") {
                        launchSingleTop = true
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("signin") {
            SignInScreen(
                onNavigateToProfile = { uid -> navController.navigate("profile/$uid") },
                onNavigateToSignup = {
                    navController.navigate("signup") {
                        launchSingleTop = true
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onSignInSuccess = { uid, isAdmin ->
                    if (isAdmin) {
                        navController.navigate("admin_dashboard") {
                            popUpTo("signin") { inclusive = true }
                            launchSingleTop = true
                        }
                    } else {
                        navController.navigate("dashboard/$uid") {
                            popUpTo("signin") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        composable(
            route = "profile/{uid}",
            arguments = listOf(navArgument("uid") { type = NavType.StringType })
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            ProfileCreationScreen(
                uid = uid,
                onNavigateToDashboard = { username, imageUri, uid ->
                    val encodedUri = Uri.encode(imageUri)
                    navController.navigate("dashboard/$username/$encodedUri/$uid") {
                        popUpTo("profile/$uid") { inclusive = true }
                    }
                }
            )
        }


        composable(
            route = "dashboard/{uid}",
            arguments = listOf(navArgument("uid") { type = NavType.StringType })
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            DashboardScreen(
                uid = uid,
                onAboutUs = { navController.navigate("about_us") },
                onOurWork = { navController.navigate("our_work") },
                onHelp = { navController.navigate("help") },
                onLogout = {
                    navController.navigate("signin") {
                        popUpTo("dashboard/$uid") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onViewDonors = { navController.navigate("view_donors/$uid") },
                onRequestBlood = { navController.navigate("request_blood/$uid") },
                onMyProfile = { navController.navigate("my_profile/$uid") },
                onBloodCampList = { navController.navigate("blood_camp_list") }
            )
        }

        composable(
            route = "dashboard/{username}/{imageUriEncoded}/{uid}",
            arguments = listOf(
                navArgument("username") { type = NavType.StringType },
                navArgument("imageUriEncoded") { type = NavType.StringType },
                navArgument("uid") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            DashboardScreen(
                uid = uid,
                onAboutUs = { navController.navigate("about_us") },
                onOurWork = { navController.navigate("our_work") },
                onHelp = { navController.navigate("help") },
                onLogout = {
                    navController.navigate("signin") {
                        popUpTo("dashboard/$uid") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onViewDonors = { navController.navigate("view_donors/$uid") },
                onRequestBlood = { navController.navigate("request_blood/$uid") },
                onMyProfile = { navController.navigate("my_profile/$uid") },
                onBloodCampList = { navController.navigate("blood_camp_list") }
            )
        }

        composable("admin_dashboard") {
            AdminDashboardScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate("signin") {
                        popUpTo("splash") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                viewModel = adminViewModel
            )
        }


        // Static Screens
        composable("about_us") {
            AboutUsScreen()
        }

        composable("our_work") {
            OurWorkScreen()
        }

        composable("help") {
            HelpScreen()
        }


        // Other screens
        // Add NavType.StringType argument for currentUserId in both routes
        composable(
            route = "view_donors/{currentUserId}",
            arguments = listOf(navArgument("currentUserId") { type = NavType.StringType })
        ) { backStackEntry ->
            val currentUserId = backStackEntry.arguments?.getString("currentUserId") ?: ""
            val bloodRequestViewModel: BloodRequestViewModel = viewModel()
            ViewDonorsScreen(
                onNavigateToChat = { chatId, currentId, requesterId ->
                    navController.navigate("chat/$chatId/$currentId/$requesterId")
                },
                onBack = { navController.popBackStack() },
                viewModel = bloodRequestViewModel,
                currentUserId = currentUserId
            )
        }

        composable(
            route = "request_blood/{currentUserId}",
            arguments = listOf(navArgument("currentUserId") { type = NavType.StringType })
        ) { backStackEntry ->
            val currentUserId = backStackEntry.arguments?.getString("currentUserId") ?: ""
            val bloodRequestViewModel: BloodRequestViewModel = viewModel()
            BloodRequestScreen(
                onBack = { navController.popBackStack() },
                onNavigateToChat = { chatId, currentId, donorId ->
                    navController.navigate("chat/$chatId/$currentId/$donorId")
                },
                viewModel = bloodRequestViewModel,
                currentUserId = currentUserId
            )
        }

        composable("blood_camp_list") {
            BloodCampListScreen()
        }

        composable(
            route = "my_profile/{uid}",
            arguments = listOf(navArgument("uid") { type = NavType.StringType })
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            MyProfileScreen(
                onBack = { navController.popBackStack() },
                uid = uid
            )
        }

        composable("chat/{chatId}/{currentUserId}/{otherUserId}") { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            val currentUserId = backStackEntry.arguments?.getString("currentUserId") ?: ""
            val otherUserId = backStackEntry.arguments?.getString("otherUserId") ?: ""

            ChatScreen(
                chatId = chatId,
                currentUserId = currentUserId,
                otherUserId = otherUserId,
                onBack = { navController.popBackStack() },
            )
        }
    }
}





