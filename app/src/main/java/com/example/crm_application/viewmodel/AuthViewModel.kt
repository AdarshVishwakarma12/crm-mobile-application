package com.example.crm_application.viewmodel

// ---- ==== Imports ==== ----

// Brings 'ViewMode', 'LiveData', 'MutableLiveData'
import android.util.Log
import androidx.lifecycle.*
import com.example.crm_application.api.GoogleLoginResponse

// importing packages
import com.example.crm_application.api.LoginRequest
import com.example.crm_application.api.LoginResponse
import com.example.crm_application.api.RetrofitInstance
import com.example.crm_application.ui.auth.GoogleLoginRequest

// Launch coroutines inside ViewModel (via ViewModelScope)
import kotlinx.coroutines.launch

// ---- ==== AuthViewModel ==== ----
// config changes
class AuthViewModel : ViewModel() {

    // Holds the response from '/login' API
    val loginResponse = MutableLiveData<LoginResponse?>()
    val loginResponseGoogle = MutableLiveData<GoogleLoginResponse>()

    // Holds error messages
    val error = MutableLiveData<String?>()

    // Function will be call by 'LoginFragment' to start login Request
    fun login(username: String, password: String) {

        // Starts coroutines
        viewModelScope.launch {
            try {

                // suspend function —> executes asynchronously
                val response = RetrofitInstance.api.login(LoginRequest(username, password))

                if(response.isSuccessful) {
                    loginResponse.value = response.body()
                } else {
                    error.value = "Login failed: ${response.code()}"
                }
            } catch (e: Exception) {
                error.value = "Error: ${e.message}"
            }
        }
    }

    // Function will be call by 'LoginFragment' to start login Request by Google
    fun googleLogin(idToken: String) {

        viewModelScope.launch {
            try {

                // suspend function -> executes asynchronously
                val response = RetrofitInstance.api.googleLogin(
                    GoogleLoginRequest("google", idToken)
                )

                if(response.isSuccessful) {
                    loginResponseGoogle.value = response.body()
                } else {
                    error.value = "Login Failed ${response.code()}"
                }
            } catch ( e : Exception) {
                error.value = "Error: ${e.message}"
            }
        }
    }
}

// ---- ==== Getting DATA from other API's [Email Login] ==== ----
// curl -X POST \
// -H "content_Type: application/json " /
// -d "{'username' : 'user_name', 'password' : 'your_password'}" \
// http://localhost:8000/api/token

// ---- ==== Getting DATA from other API's [Google Login] ==== ----
// curl -H "Authorization: Token 387f2a3ea907e34e0fc10165fe24920a0d8e75f5" \
// http://192.168.226.102:8000/api/clients/