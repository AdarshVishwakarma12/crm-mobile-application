package com.example.crm_application.ui.leads

data class LeadsData(
    val id: Int,
    val name: String,
    val email: String?,
    val country_code: String?,
    val phone: String?,
    val address: String?,
    val description: String?,
    val is_active: Boolean,
    val created_at: String,
    val last_edit: String,
    val created_by_user: Int?,
    val list: Int?,
    val companyAssignee: Int?
)