package com.example.crm_application.ui.auth

data class GoogleLoginRequest(
    val provider: String = "google",
    val access_token: String
)