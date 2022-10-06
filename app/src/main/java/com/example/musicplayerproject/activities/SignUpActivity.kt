package com.example.musicplayerproject.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayerproject.R
import com.example.musicplayerproject.databinding.ActivitySignUpBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private lateinit var signUpRequest: BeginSignInRequest
    private  lateinit var signUpBinding: ActivitySignUpBinding
    private lateinit var signUpClient: GoogleSignInClient


    var gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(R.string.client_id.toString())
        .requestEmail()
        .build()

    companion object {
        const val SIGN_UP_GOOGLE_REQUEST_CODE = 123
        const val TAG = "SIGN UP ACTIVITY"
    }

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signUpBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(signUpBinding.root)
        supportActionBar?.hide()

        auth = Firebase.auth
        signUpClient = GoogleSignIn.getClient(this, gso)

    }

    fun onSignUp(view: View) {


    }

    fun signUpWithGoogle(view: View) {
//        signUpRequest = BeginSignInRequest.builder()
//                .setGoogleIdTokenRequestOptions(
//                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                                .setSupported(true)
//                                // Your server's client ID, not your Android client ID.
//                                .setServerClientId(getString(R.string.client_id))
//                                // Only show accounts previously used to sign in.
//                                .setFilterByAuthorizedAccounts(true)
//                                .build()
//                ).build()

        var signUpWithGoogleIntent = signUpClient.signInIntent
        startActivityForResult(signUpWithGoogleIntent, SIGN_UP_GOOGLE_REQUEST_CODE)

    }
    fun signUpWithFacebook(view: View) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == SIGN_UP_GOOGLE_REQUEST_CODE)
        {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        Toast.makeText(this, "Login successfully", Toast.LENGTH_LONG).show()
    }


}