package com.example.crm_application.api

import retrofit2.http.*
import retrofit2.Response

// ---- ==== Data Classes for LOGIN Request and Response! ==== ----
// When user log in we send the {"username": "", "password": ""}
// And In response we GET back the 'access' token (JWT access token) and 'refresh' token
data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val access: String, val refresh: String)

// ---- ==== Data Classes for SIGNUP Request and Response ==== ----
data class SignUpRequest(val username: String, val email: String, val password: String)
data class SignUpResponse(val id: Int, val username: String, val email: String)

// ---- ==== RETROFIT API CALLS ==== ----
// the interface tell retrofit, how to make network calls to the django backend
interface ApiServices {

    // Calls Django Backend -> Sends a JSON body with the login credentials
    @POST("api/token/")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // Registering New User
    @POST("api/signup")
    suspend fun signup(@Body request: SignUpRequest): Response<SignUpResponse>

    // Calls Protected Dashboard
    // Sends the Authorization header with the token
    @GET("api/dashboard/")
    suspend fun getDashboard(@Header("Authorization") token: String): Response<DashboardResponse>
}

// ==== ---- Dashboard data class ---- ====
data class DashboardResponse(
    val totalLeads: Int,
    val totalTasks: Int,
    val totalRoles: Int
)