package com.example.crm_application.api

// ---- ==== Imports ==== ----
import retrofit2.Retrofit

// JSON < - > Kotlin Data Classes
import retrofit2.converter.gson.GsonConverterFactory

// Handle all Network calls, and log them
import okhttp3.OkHttpClient

// Let see the Request and Response in Logcat
import okhttp3.logging.HttpLoggingInterceptor

// ---- ==== Singleton object ==== ----
object RetrofitInstance {

    // Base url of Django Application
    // while using emulator
     private const val BASE_URL = "http://10.0.2.2:8000/"

    // while connecting with real device -> ipconfig getifaddr en0 && python manage.py runserver 0.0.0.0:8000
//    private const val BASE_URL = "http://192.168.1.40:8000/"

    // while at development
//    private const val BASE_URL = "https://ctrlcrm.pythonanywhere.com"

    // Custom HTTP Client
    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }).build()

    // Create Retrofit Object
    val api: ApiServices by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServices::class.java)
    }
}