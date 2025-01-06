package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.HomeActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore


    private val signInResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            Log.d("patates",result.resultCode.toString())
            if (result.resultCode == RESULT_OK){
                Log.d("patates","oldu")
                val signInIntent = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(signInIntent)
                try {
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account)
                } catch (e: ApiException) {

                }
            }else{
                Log.d("patates","olmadı")
            }
        }

    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("294769362221-usbjudf9p6kqtit952ftr4br5btfmsa0.apps.googleusercontent.com")
        .requestEmail()
        .build()



    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity
    private var showOneTapUI = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)
        firestore = FirebaseFirestore.getInstance()

        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance()
        googleSignInClient = GoogleSignIn.getClient(this,gso)
//        var signInRequest = BeginSignInRequest.builder()
//            .setGoogleIdTokenRequestOptions(
//                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                    .setSupported(true)
//                    .setServerClientId(getString(R.string.your_web_client_id))
//                    .setFilterByAuthorizedAccounts(true)
//                    .build()
//            )
//            .build()
        
        // Connect views
        val etEmail = findViewById<EditText>(R.id.et_Mail)
        val etPassword = findViewById<EditText>(R.id.et_Sifre)
        val btnLogin = findViewById<Button>(R.id.btn_Login)

        // Login button click listener
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Check if fields are empty
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Log in the user
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Login successful, navigate to HomePageActivity
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Login failed
                        Toast.makeText(this, "User not found. Please check your details or sign up.", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Diğer sayfalara yönlendirme işlemleri
        val tvSignUp = findViewById<TextView>(R.id.tv_Kayit)
        tvSignUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val tvForgotPassword = findViewById<TextView>(R.id.tv_forgot_password)
        tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()

        googleSignInClient = GoogleSignIn.getClient(this , gso)

        findViewById<Button>(R.id.gSignInBtn).setOnClickListener{
            signIn()
        }

    }

    private fun saveUserToFirestore(userId: String, email: String?, name: String?) {
            val userMap = hashMapOf(
                "userId" to userId,
                "email" to email,
                "name" to name
            )

            firestore.collection("users")
                .document(userId) // userId ile kayıt ediyoruz
                .set(userMap)
                .addOnSuccessListener {
                    Log.d("Firestore", "User saved successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error saving user", e)
                }

    }


    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        signInResultLauncher.launch(signInIntent)
    }


    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        if (account != null) {

            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = auth.currentUser
                        saveUserToFirestore(
                            userId = firebaseUser?.uid ?: "",
                            email = firebaseUser?.email,
                            name = firebaseUser?.displayName
                        )

                        val intent : Intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {

                        Toast
                            .makeText(this,"${task.exception?.message}",Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }
    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent

        launcher.launch(signInIntent)

    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
                if (result.resultCode==Activity.RESULT_OK){

                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    handleResult(task)
                }else{
                    Log.d("patates","result okay")
                }

    }

    private fun handleResult(task: Task<GoogleSignInAccount>) {
        if(task.isSuccessful){
            Log.d("patates","successful")
            val account : GoogleSignInAccount ?= task.result
            if (account!= null){
                Log.d("patates","account called")
                updateUI(account)
            }
        }else{
            Log.d("patates","unsuccessful")
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener{
            if (it.isSuccessful){
                val intent : Intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

            }
        }
    }
}
