package com.example.myapplication

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.io.Serializable

data class User(
    val user: FirebaseUser,
    val auth: FirebaseAuth
)