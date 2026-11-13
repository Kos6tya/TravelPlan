package com.example.travelplan.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    // Поточний користувач (null, якщо не увійшов)
    var currentUser by mutableStateOf(auth.currentUser)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun signIn(email: String, pass: String, onSuccess: () -> Unit) {
        if (!validate(email, pass)) return
        isLoading = true
        errorMessage = null

        auth.signInWithEmailAndPassword(email.trim(), pass)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    currentUser = auth.currentUser
                    onSuccess()
                } else {
                    errorMessage = task.exception?.localizedMessage ?: "Помилка входу. Перевірте пароль."
                }
            }
    }

    fun signUp(email: String, pass: String, onSuccess: () -> Unit) {
        if (!validate(email, pass)) return
        isLoading = true
        errorMessage = null

        auth.createUserWithEmailAndPassword(email.trim(), pass)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    currentUser = auth.currentUser
                    onSuccess()
                } else {
                    errorMessage = task.exception?.localizedMessage ?: "Помилка реєстрації."
                }
            }
    }

    fun signOut() {
        auth.signOut()
        currentUser = null
    }

    private fun validate(email: String, pass: String): Boolean {
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            errorMessage = "Будь ласка, введіть коректний Email"
            return false
        }
        if (pass.length < 6) {
            errorMessage = "Пароль має містити щонайменше 6 символів"
            return false
        }
        return true
    }

    fun clearError() {
        errorMessage = null
    }
}