package com.example.crm_application.viewmodel

// ---- ==== Imports ==== ----

// Brings 'ViewMode', 'LiveData', 'MutableLiveData'
import androidx.lifecycle.*

// importing packages
import com.example.crm_application.api.LoginRequest
import com.example.crm_application.api.LoginResponse
import com.example.crm_application.api.RetrofitInstance

// Launch coroutines inside ViewModel (via ViewModelScope)
import kotlinx.coroutines.launch

// ---- ==== AuthViewModel ==== ----
// config changes
class AuthViewModel : ViewModel() {

    // Holds the response from '/login' API
    val loginResponse = MutableLiveData<LoginResponse?>()

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
}