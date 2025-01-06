package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()

        val emailEditText = findViewById<EditText>(R.id.et_email_reset)
        val resetButton = findViewById<Button>(R.id.btn_reset_password)

        resetButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email address.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "A password reset link has been sent to your email address.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "An error occurred: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        // BACK butonu için tıklama olayını ayarla
        val btnBack2 = findViewById<Button>(R.id.btnBack2)
        btnBack2.setOnClickListener {
            finish()
        }
    }
}
