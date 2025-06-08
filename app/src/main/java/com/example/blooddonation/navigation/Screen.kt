package com.example.blooddonation.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object SignUp : Screen("signup")
    data object SignIn : Screen("signin")
    data object AdminDashboard : Screen("admin_dashboard")

    data object Profile : Screen("profile") {
        const val ARG_UID = "uid"
        const val routeWithArgs = "profile/{$ARG_UID}"
        fun createRoute(uid: String) = "profile/$uid"
    }

    data object Dashboard : Screen("dashboard") {
        const val ARG_UID = "uid"
        const val ARG_USERNAME = "username"
        const val ARG_IMAGE_URI = "imageUriEncoded"
        const val routeWithUid = "dashboard/{$ARG_UID}"
        const val routeWithProfile = "dashboard/{$ARG_USERNAME}/{$ARG_IMAGE_URI}/{$ARG_UID}"
        fun createRoute(uid: String) = "dashboard/$uid"
        fun createRoute(username: String, imageUriEncoded: String, uid: String) =
            "dashboard/$username/$imageUriEncoded/$uid"
    }

    data object AboutUs : Screen("about_us")
    data object OurWork : Screen("our_work")
    data object Help : Screen("help")

    data object ViewRequests : Screen("view_requests") {
        const val ARG_USER = "currentUserId"
        const val routeWithArgs = "view_requests/{$ARG_USER}"
        fun createRoute(uid: String) = "view_requests/$uid"
    }

    data object RequestBlood : Screen("request_blood") {
        const val ARG_USER = "currentUserId"
        const val routeWithArgs = "request_blood/{$ARG_USER}"
        fun createRoute(uid: String) = "request_blood/$uid"
    }

    data object MyProfile : Screen("my_profile") {
        const val ARG_UID = "uid"
        const val routeWithArgs = "my_profile/{$ARG_UID}"
        fun createRoute(uid: String) = "my_profile/$uid"
    }

    data object BloodCampList : Screen("blood_camp_list")

    data object Chat : Screen("chat") {
        const val ARG_CHAT_ID = "chatId"
        const val ARG_CURRENT_ID = "currentUserId"
        const val ARG_OTHER_ID = "otherUserId"
        const val routeWithArgs = "chat/{$ARG_CHAT_ID}/{$ARG_CURRENT_ID}/{$ARG_OTHER_ID}"
        fun createRoute(chatId: String, currentId: String, otherId: String) =
            "chat/$chatId/$currentId/$otherId"
    }
}
