package com.example.crm_application.ui.auth

data class OutlookLoginRequest(
    val provider: String,
    val access_token: String
)