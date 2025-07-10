package com.example.crm_application.ui.leads

import android.util.Log
import androidx.lifecycle.*
import com.example.crm_application.api.RetrofitInstance
import kotlinx.coroutines.launch
import java.lang.Exception

class LeadsViewModel : ViewModel() {
    val leads = MutableLiveData<List<LeadsData>>()
    val error = MutableLiveData<String?>()

    fun loadLeads(token: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getLeadsList("Bearer $token")
                if(response.isSuccessful) {
                    leads.value = response.body()
                    Log.i("MODEL", "I am here ${leads.value}")
                } else {
                    error.value = "Failed: ${response.code()}"
                }
            } catch (e : Exception) {
                error.value = "Error: ${e.message}"
            }
        }
    }
}