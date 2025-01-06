package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.HomeActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.util.jar.Attributes.Name

class ProfileSettingsActivity : AppCompatActivity() {

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var updateProfileButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    private val firestore = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Firebase Authentication başlatma
        auth = FirebaseAuth.getInstance()

        // View'ları bağlama
        firstNameEditText = findViewById(R.id.firstNameEditText)
        lastNameEditText = findViewById(R.id.lastNameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        updateProfileButton = findViewById(R.id.updateProfileButton)

        //kullanıcı bilgilerini getirme
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = firestore.collection("Users").document(userId)
        ref.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot != null) {

                val firstName = documentSnapshot.getString("Name") ?: ""
                val lastName = documentSnapshot.getString("Surname") ?: ""
                val email = documentSnapshot.getString("E-mail") ?: ""
                val password = documentSnapshot.getString("Password") ?: ""


                firstNameEditText.setText(firstName)
                lastNameEditText.setText(lastName)
                emailEditText.setText(email)
                passwordEditText.setText(password)
            }
        }
            .addOnFailureListener{
                Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
            }



        // Profil güncelleme butonu
        updateProfileButton.setOnClickListener {
            val sName = firstNameEditText.text.toString()
            val sSurname = lastNameEditText.text.toString()
            val sEmail = emailEditText.text.toString()
            val sPassword = passwordEditText.text.toString()

            val updateMap = mapOf(
                "Name" to sName,
                "Surname" to sSurname,
                "E-mail" to sEmail,
                "Password" to sPassword
            )
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            firestore.collection("Users").document(userId).update(updateMap)
            Toast.makeText(this, "Successfuly Edited!", Toast.LENGTH_SHORT).show()

        }

        // Geri butonu
        val backButton = findViewById<Button>(R.id.btnBack)
        backButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        // Çıkış butonu
        val exitButton: Button = findViewById(R.id.exit)
        exitButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }



    private fun updateUserProfile() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val updatedData = mapOf(
                "firstName" to firstNameEditText.text.toString(),
                "lastName" to lastNameEditText.text.toString(),
                "email" to emailEditText.text.toString()
            )

            // Firestore'da güncelleme
            firestore.collection("users").document(userId).update(updatedData)
                .addOnSuccessListener {
                    // Şifreyi güncelleme (isteğe bağlı)
                    val newPassword = passwordEditText.text.toString()
                    if (newPassword.isNotEmpty()) {
                        auth.currentUser?.updatePassword(newPassword)?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Failed to update password.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update profile.", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
        }
    }
}
