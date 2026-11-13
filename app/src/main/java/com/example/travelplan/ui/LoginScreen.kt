package com.example.travelplan.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onAuthSuccess: () -> Unit
) {
    var isLoginMode by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (isLoginMode) "Вхід у TravelPlan ✈️" else "Реєстрація 🌍",
                    style = MaterialTheme.typography.headlineSmall
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        authViewModel.clearError()
                    },
                    label = { Text("Email") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        authViewModel.clearError()
                    },
                    label = { Text("Пароль (від 6 символів)") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )

                authViewModel.errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (authViewModel.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(36.dp))
                } else {
                    Button(
                        onClick = {
                            if (isLoginMode) {
                                authViewModel.signIn(email, password, onAuthSuccess)
                            } else {
                                authViewModel.signUp(email, password, onAuthSuccess)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isLoginMode) "Увійти" else "Зареєструватися")
                    }
                }

                TextButton(
                    onClick = {
                        isLoginMode = !isLoginMode
                        authViewModel.clearError()
                    }
                ) {
                    Text(
                        if (isLoginMode) "Немає акаунту? Зареєструватися"
                        else "Вже маєте акаунт? Увійти"
                    )
                }
            }
        }
    }
}