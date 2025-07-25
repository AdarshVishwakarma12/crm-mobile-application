package com.example.crm_application.outlook

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.android.volley.Response
import com.example.crm_application.R
import com.microsoft.identity.client.AcquireTokenParameters
import com.microsoft.identity.client.AcquireTokenSilentParameters
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAccount
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.IPublicClientApplication
import com.microsoft.identity.client.IPublicClientApplication.ISingleAccountApplicationCreatedListener
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.client.SignInParameters
import com.microsoft.identity.client.SilentAuthenticationCallback
import com.microsoft.identity.client.exception.MsalException
import org.json.JSONObject

class OutlookSingleAccountModeAuth(
    private val context: Context,
    private val activity: Activity,
    private val graphResourceUrl: String = MSGraphRequestWrapper.MS_GRAPH_ROOT_ENDPOINT + "v1.0/me"
) {

    private var msalApp: ISingleAccountPublicClientApplication? = null
    private var currentAccount: IAccount? = null

    private val TAG = "OutlookAuth"

    private val scopes = arrayOf("User.Read")

    fun initialize(onInitialized: (() -> Unit)? = null, onError: ((Exception) -> Unit)? = null) {
        PublicClientApplication.createSingleAccountPublicClientApplication(
            context,
            R.raw.auth_config_single_account,
            object : IPublicClientApplication.ISingleAccountApplicationCreatedListener {
                override fun onCreated(application: ISingleAccountPublicClientApplication) {
                    msalApp = application
                    loadAccount()
                    // onInitialized?.invoke()
                }

                override fun onError(exception: MsalException) {
                    Log.e(TAG, "Initialization failed: $exception")
                    // onError?.invoke(exception)
                }
            }
        )
    }

    fun signIn(onSuccess: (IAuthenticationResult) -> Unit, onError: (Exception) -> Unit) {

        msalApp?.getCurrentAccountAsync(object : ISingleAccountPublicClientApplication.CurrentAccountCallback {
            override fun onAccountLoaded(account: IAccount?) {
                if (account != null) {
                    // Account exists, sign out first
                    msalApp?.signOut(object : ISingleAccountPublicClientApplication.SignOutCallback {
                        override fun onSignOut() {
                            Log.d(TAG, "Signed out successfully, now signing in.")
                            doSignIn(onSuccess, onError)
                        }

                        override fun onError(exception: MsalException) {
                            Log.e(TAG, "Sign out failed: $exception")
                            // Even if sign out fails, try sign in anyway
                            doSignIn(onSuccess, onError)
                        }
                    })
                } else {
                    // No account, just sign in
                    doSignIn(onSuccess, onError)
                }
            }

            override fun onAccountChanged(
                priorAccount: IAccount?,
                currentAccount: IAccount?
            ) { }

            override fun onError(exception: MsalException) {
                Log.e(TAG, "Failed to get current account: $exception")
                // Just try sign in anyway
                doSignIn(onSuccess, onError)
            }
        })
    }

    // Helper function to do the actual sign in
    private fun doSignIn(onSuccess: (IAuthenticationResult) -> Unit, onError: (Exception) -> Unit) {
        val signInParameters = SignInParameters.builder()
            .withActivity(activity)
            .withScopes(scopes.asList())
            .withCallback(object : AuthenticationCallback {
                override fun onSuccess(authenticationResult: IAuthenticationResult) {
                    currentAccount = authenticationResult.account
                    onSuccess(authenticationResult)
                }

                override fun onError(exception: MsalException) {
                    Log.e(TAG, "Sign-in failed: $exception")
                    onError(exception)
                }

                override fun onCancel() {
                    Log.d(TAG, "User cancelled login.")
                    Toast.makeText(context, "Login cancelled", Toast.LENGTH_SHORT).show()
                }
            })
            .build()

        msalApp?.signIn(signInParameters)
    }

    fun signOut(onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        msalApp?.signOut(object : ISingleAccountPublicClientApplication.SignOutCallback {
            override fun onSignOut() {
                currentAccount = null
                onSuccess()
            }

            override fun onError(exception: MsalException) {
                Log.e(TAG, "Sign-out error: $exception")
                onError(exception)
            }
        })
    }

    fun callGraphApiInteractive(onResult: (JSONObject) -> Unit, onError: (Exception) -> Unit) {
        val parameters = AcquireTokenParameters.Builder()
            .startAuthorizationFromActivity(activity)
            .withScopes(scopes.asList())
            .withCallback(object : AuthenticationCallback {
                override fun onSuccess(authenticationResult: IAuthenticationResult) {
                    currentAccount = authenticationResult.account
                    callGraphApi(authenticationResult.accessToken, onResult, onError)
                }

                override fun onError(exception: MsalException) {
                    Log.e(TAG, "Interactive token acquisition failed: $exception")
                    onError(exception)
                }

                override fun onCancel() {
                    Log.d(TAG, "User cancelled token request.")
                }
            })
            .forAccount(currentAccount)
            .build()

        msalApp?.acquireToken(parameters)
    }

    fun callGraphApiSilent(onResult: (JSONObject) -> Unit, onError: (Exception) -> Unit) {
        val parameters = AcquireTokenSilentParameters.Builder()
            .fromAuthority(currentAccount?.authority)
            .forAccount(currentAccount)
            .withScopes(scopes.asList())
            .withCallback(object : SilentAuthenticationCallback {
                override fun onSuccess(authenticationResult: IAuthenticationResult) {
                    callGraphApi(authenticationResult.accessToken, onResult, onError)
                }

                override fun onError(exception: MsalException) {
                    Log.e(TAG, "Silent token acquisition failed: $exception")
                    onError(exception)
                }
            })
            .build()

        msalApp?.acquireTokenSilentAsync(parameters)
    }

    private fun loadAccount() {
        msalApp?.getCurrentAccountAsync(object : ISingleAccountPublicClientApplication.CurrentAccountCallback {
            override fun onAccountLoaded(activeAccount: IAccount?) {
                currentAccount = activeAccount
            }

            override fun onAccountChanged(priorAccount: IAccount?, currentAccount: IAccount?) {
                if (currentAccount == null) {
                    Toast.makeText(context, "Signed out", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onError(exception: MsalException) {
                Log.e(TAG, "Error loading account: $exception")
            }
        })
    }

    private fun callGraphApi(token: String, onResult: (JSONObject) -> Unit, onError: (Exception) -> Unit) {
        MSGraphRequestWrapper.callGraphAPIUsingVolley(
            context,
            graphResourceUrl,
            token,
            Response.Listener { response -> onResult(response) },
            Response.ErrorListener { error -> onError(error) }
        )
    }

    fun getCurrentAccount(): IAccount? = currentAccount
}