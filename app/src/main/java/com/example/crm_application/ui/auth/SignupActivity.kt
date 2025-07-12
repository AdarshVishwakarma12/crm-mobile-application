package com.example.crm_application.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.crm_application.R
import com.example.crm_application.viewmodel.AuthViewModel
import androidx.core.content.edit

class SignupActivity : AppCompatActivity() {

    private lateinit var userNameEdit: EditText
    private lateinit var userEmailEdit: EditText
    private lateinit var userPasswordEdit: EditText
    private lateinit var signUpButton: Button
    private lateinit var errorText: TextView
    private lateinit var loginText: TextView
    private lateinit var progressBar: ProgressBar

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.fragment_signup)

        userNameEdit = findViewById(R.id.usernameEdit)
        userEmailEdit = findViewById(R.id.emailEdit)
        userPasswordEdit = findViewById(R.id.passwordEdit)
        signUpButton = findViewById(R.id.signupButton)
        errorText = findViewById(R.id.errorTextSignUp)
        loginText = findViewById(R.id.loginText)
        progressBar = findViewById(R.id.signUpProgress)

        loginText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        signUpButton.setOnClickListener {
            val username = userNameEdit.text.toString()
            val email = userEmailEdit.text.toString()
            val password = userPasswordEdit.text.toString()

            if(checkValididity(username, email, password)) {
                signUpButton.isEnabled = false
                progressBar.visibility = View.VISIBLE

                // Using Handler postdelayed
                Handler(Looper.getMainLooper()).postDelayed({

                    // Operation to perform
                    Toast.makeText(this, "Clicked Signup button", Toast.LENGTH_SHORT).show()

                    // what to do after?
                    signUpButton.isEnabled = true
                    progressBar.visibility = View.GONE
                }, 1000)
            }

            // viewModel.signup(username, password)
        }

//        viewModel.loginResponse.observe(this, Observer { response ->
//            response?.let {
//                val token = it.access
//                getSharedPreferences("app_prefs", MODE_PRIVATE)
//                    .edit {
//                        putString("access_token", token)
//                    }
//
//                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
//            }
//        })
//
//        viewModel.error.observe(this, Observer { err ->
//            errorText.text = err ?: ""
//            errorText.visibility = View.VISIBLE
//        })
    }

    private fun checkValididity(param1: String, param2: String, param3: String) : Boolean{
        return true
    }
}