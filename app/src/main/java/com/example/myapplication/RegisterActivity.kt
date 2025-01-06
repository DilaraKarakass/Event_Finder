//package com.example.myapplication
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Button
//import android.widget.EditText
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.google.firebase.auth.FirebaseAuth
//
//class RegisterActivity : AppCompatActivity() {
//
//    private lateinit var auth: FirebaseAuth
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_register_page)
//
//        // FirebaseAuth instance'ını al
//        auth = FirebaseAuth.getInstance()
//
//        // View'ları bağla
//        val etFirstName = findViewById<EditText>(R.id.et_Isim)
//        val etLastName = findViewById<EditText>(R.id.et_SoyIsim)
//        val etEmail = findViewById<EditText>(R.id.et_Mail)
//        val etPassword = findViewById<EditText>(R.id.et_Sifre)
//        val etPasswordAgain = findViewById<EditText>(R.id.etSifreTekrar)
//        val btnSignUp = findViewById<Button>(R.id.btnKayitOl)
//        val btnBack = findViewById<Button>(R.id.btnBack)
//
//        btnBack.setOnClickListener {
//            // Login sayfasına yönlendirme
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish() // Bu aktiviteyi kapat (isteğe bağlı)
//        }
//
//        // Sign Up butonuna tıklama
//        btnSignUp.setOnClickListener {
//            val firstName = etFirstName.text.toString().trim()
//            val lastName = etLastName.text.toString().trim()
//            val email = etEmail.text.toString().trim()
//            val password = etPassword.text.toString().trim()
//            val passwordAgain = etPasswordAgain.text.toString().trim()
//
//            // Alanlar boş mu kontrol et
//            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || passwordAgain.isEmpty()) {
//                Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            // Şifreler eşleşiyor mu kontrol et
//            if (password != passwordAgain) {
//                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            // Firebase'e kullanıcı kaydet
//            auth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        // Kayıt başarılı
//                        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
//                        // Kullanıcıyı giriş sayfasına yönlendirebilirsiniz.
//                        finish() // Bu aktiviteyi kapatabilirsiniz.
//                    } else {
//                        // Kayıt başarısız
//                        Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
//                    }
//                }
//        }
//    }
//}
//
package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_page)

        // FirebaseAuth ve Firestore instance'larını al
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // View'ları bağla
        val etFirstName = findViewById<EditText>(R.id.et_Isim)
        val etLastName = findViewById<EditText>(R.id.et_SoyIsim)
        val etEmail = findViewById<EditText>(R.id.et_Mail)
        val etPassword = findViewById<EditText>(R.id.et_Sifre)
        val etPasswordAgain = findViewById<EditText>(R.id.etSifreTekrar)
        val btnSignUp = findViewById<Button>(R.id.btnKayitOl)
        val btnBack = findViewById<Button>(R.id.btnBack)

        btnBack.setOnClickListener {
            // Login sayfasına yönlendirme
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Bu aktiviteyi kapat
        }

        // Sign Up butonuna tıklama
        btnSignUp.setOnClickListener {
            val firstName = etFirstName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val passwordAgain = etPasswordAgain.text.toString().trim()

            // Alanlar boş mu kontrol et
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || passwordAgain.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Şifreler eşleşiyor mu kontrol et
            if (password != passwordAgain) {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            // Firebase'e kullanıcı kaydet
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Kayıt başarılı, UID'yi al
                        val userId = auth.currentUser?.uid
                        if (userId != null) {
                            // Kullanıcı bilgilerini Firestore'a ekle
                            val user = hashMapOf(
                                "Name" to firstName,
                                "Surname" to lastName,
                                "E-mail" to email,
                                "Password" to password
                            )

                            firestore.collection("Users").document(userId).set(user)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        "Registration successful! User data saved.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // Kullanıcıyı giriş sayfasına yönlendirebilirsiniz
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        this,
                                        "Registration successful, but Firestore save failed: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    } else {
                        // Kayıt başarısız
                        Toast.makeText(
                            this,
                            "Registration failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}
