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
import com.example.crm_application.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var userNameEdit: EditText
    private lateinit var userPasswordEdit: EditText
    private lateinit var loginButton: Button
    private lateinit var errorText: TextView
    private lateinit var signUpText: TextView
    private lateinit var progressBar: ProgressBar

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.fragment_login) // or use activity_login.xml if that's your layout

        userNameEdit = findViewById(R.id.emailEdit)
        userPasswordEdit = findViewById(R.id.passwordEdit)
        loginButton = findViewById(R.id.loginButton)
        errorText = findViewById(R.id.errorTextLogIn)
        signUpText = findViewById(R.id.signUpText)
        progressBar = findViewById(R.id.loginProgress)


        loginButton.setOnClickListener {

            val username = userNameEdit.text.toString()
            val password = userPasswordEdit.text.toString()

            if(checkValididity(username, password)) {
                loginButton.isEnabled = false
                progressBar.visibility = View.VISIBLE

                // Using Handler postdelayed
                Handler(Looper.getMainLooper()).postDelayed({

                    // Operation to perform
                    viewModel.login(username, password)

                    // what to do after?
                    loginButton.isEnabled = true
                    progressBar.visibility = View.GONE
                }, 1000)
            }
        }

        signUpText.setOnClickListener {
            // Change to Sign Up Activity
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        viewModel.loginResponse.observe(this, Observer { response ->
            response?.let {
                val token = it.access
                getSharedPreferences("app_prefs", MODE_PRIVATE)
                    .edit {
                        putString("access_token", token)
                    }

                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                val loginIntent = Intent(this, MainActivity::class.java)
                startActivity(loginIntent)
            }
        })

        viewModel.error.observe(this, Observer { err ->
            errorText.text = err ?: ""
            errorText.visibility = View.VISIBLE
        })
    }

    private fun checkValididity(param1: String, param2: String) : Boolean {
        return true;
    }
}