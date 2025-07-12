package com.example.crm_application.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
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
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.Runnable

class LoginActivity : AppCompatActivity() {

    private lateinit var userNameEdit: EditText
    private lateinit var userPasswordEdit: EditText
    private lateinit var loginButton: Button
    private lateinit var errorText: TextView
    private lateinit var signUpText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var googleSignIn: ImageButton

    private val RC_SIGN_IN = 1001

    private val viewModel: AuthViewModel by viewModels()

    companion object {
        const val TAG = "LoginActivity"
    }

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
        googleSignIn = findViewById(R.id.googleSignIn)


        // Token Based Authentication
        loginButton.setOnClickListener {

            val username = userNameEdit.text.toString()
            val password = userPasswordEdit.text.toString()

            if(checkValidity(username, password)) {
                loginButton.isEnabled = false
                progressBar.visibility = View.VISIBLE

                // Operation to perform
                viewModel.login(username, password)

                // Using Handler post-delayed
                Handler(Looper.getMainLooper()).postDelayed({

                    // what to do after?
                    loginButton.isEnabled = true
                    progressBar.visibility = View.GONE

                }, 1000)

            }
        }

        // Session Based Authentication
        googleSignIn.setOnClickListener {

            Toast.makeText(this, "Continue with Google", Toast.LENGTH_SHORT).show()

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // from google-services.json
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(this, gso)

            loginButton.isEnabled = false
            progressBar.visibility = View.VISIBLE

            // Operation to perform
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)

            // Using Handler post-delayed
            Handler(Looper.getMainLooper()).postDelayed({

                // what to do after?
                loginButton.isEnabled = true
                progressBar.visibility = View.GONE

            }, 1000)

        }

        signUpText.setOnClickListener {
            // Change to Sign Up Activity
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }

        viewModel.loginResponse.observe(this, Observer { response ->
            response?.let {
                val token = it.access
                getSharedPreferences("app_prefs", MODE_PRIVATE)
                    .edit {
                        putString("access_token", token)
                    }

                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                val mainActivityIntent = Intent(this, MainActivity::class.java)
                startActivity(mainActivityIntent)
                finish()
            }
        })

        viewModel.loginResponseGoogle.observe(this, Observer { response ->
            response?.let {
                val key = it.key
                getSharedPreferences("app_prefs", MODE_PRIVATE)
                    .edit() {
                        putString("key", key)
                    }

                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                val mainActivityIntent = Intent(this, MainActivity::class.java)
                startActivity(mainActivityIntent)
                finish()

            }
        })

        viewModel.error.observe(this, Observer { err ->
            errorText.text = err ?: ""
            errorText.visibility = View.VISIBLE
        })
    }

    private fun checkValidity(param1: String, param2: String) : Boolean {
        return true;
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken

                val serverAuthCode = account?.serverAuthCode
                Log.i("GOOGLE_AUTH_CODE", "Code: $serverAuthCode")

                Log.d(TAG, "ID Token: $idToken")
                Log.d(TAG, "Account: $account")

                getAccessToken(account)

            } catch (e : ApiException) {
                Log.e(TAG, "Error: $e")
                Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getAccessToken(account: GoogleSignInAccount?) {

        Log.i(TAG, "inside getAccessToken, account: $account")

        account?.let {
            val scope = "oauth2:profile email"

            // Defining the Thread
            val runnable = Runnable {
                try {

                    Log.i(TAG, "requesting for token!")

                    val token = GoogleAuthUtil.getToken(this, account.account!!, scope)

                    Log.i(TAG, "access token: $token")

                    // SEND to viewModel
                    val response = viewModel.googleLogin(token.toString())

                    Toast.makeText(this, "Successful Login!", Toast.LENGTH_SHORT).show()

                } catch (e : Exception) {
                    Log.e("GoogleAccessToken", "Failed: ${e.message}")
                }
            }

            // Starting the Thread
            Thread(runnable).start()
        }
    }
}