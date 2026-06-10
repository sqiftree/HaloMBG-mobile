package com.halombg.mobile

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.halombg.mobile.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    preselectedRole: String? = null,
    onLoginSuccess: (String, String) -> Unit,
    onCancel: () -> Unit
) {
    val viewModel: LoginViewModel = viewModel()
    val context = LocalContext.current

    // Sync login success
    LaunchedEffect(viewModel.loginSuccess) {
        if (viewModel.loginSuccess) {
            onLoginSuccess(viewModel.userRoleForNavigation ?: "Siswa", viewModel.email)
            viewModel.clearState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface2) // Flat Surface2 background (#F8F7F5)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "HaloMBG",
                color = PrimaryNavy,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                text = "Portal Monitoring Program Makan Bergizi",
                color = TextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                shape = RoundedCornerShape(8.dp), // Card radius 8dp per DESIGN.md
                colors = CardDefaults.cardColors(containerColor = Surface1), // Surface1 (#FFFFFF)
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Masuk ke Akun Anda",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Error Alert Message Component
                    AnimatedVisibility(visible = viewModel.errorMessage != null) {
                        viewModel.errorMessage?.let { error ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                shape = RoundedCornerShape(4.dp), // Status/Badge corner radius 4dp
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)) // Light Red
                            ) {
                                Text(
                                    text = error,
                                    color = StatusError, // Deep Red
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    // Email Field
                    Text(
                        text = "Surel (Email)",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    OutlinedTextField(
                        value = viewModel.email,
                        onValueChange = { viewModel.email = it },
                        placeholder = { Text("operator@halombg.go.id", color = TextTertiary, fontSize = 14.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        shape = RoundedCornerShape(6.dp), // Input corner radius 6dp
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Surface1,
                            unfocusedContainerColor = Surface1,
                            focusedIndicatorColor = PrimaryNavy,
                            unfocusedIndicatorColor = BorderDefault,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true,
                        enabled = !viewModel.isLoading,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    // Password Field
                    Text(
                        text = "Kata Sandi",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    OutlinedTextField(
                        value = viewModel.password,
                        onValueChange = { viewModel.password = it },
                        placeholder = { Text("******", color = TextTertiary, fontSize = 14.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        shape = RoundedCornerShape(6.dp), // Input corner radius 6dp
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Surface1,
                            unfocusedContainerColor = Surface1,
                            focusedIndicatorColor = PrimaryNavy,
                            unfocusedIndicatorColor = BorderDefault,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true,
                        enabled = !viewModel.isLoading,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    // Simulation Mode Checkbox
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Checkbox(
                            checked = viewModel.isSimulationMode,
                            onCheckedChange = { if (!viewModel.isLoading) viewModel.isSimulationMode = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = PrimaryNavy,
                                uncheckedColor = TextTertiary
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Gunakan Mode Simulasi (Offline)",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }

                    // Submit Button
                    Button(
                        onClick = { viewModel.login() },
                        enabled = !viewModel.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp)
                            .height(44.dp),
                        shape = RoundedCornerShape(6.dp), // Button corner radius 6dp
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryNavy,
                            disabledContainerColor = PrimaryNavy.copy(alpha = 0.6f)
                        )
                    ) {
                        if (viewModel.isLoading) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    color = Surface1,
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Menghubungkan...", color = Surface1, fontSize = 14.sp)
                            }
                        } else {
                            Text("Masuk ke Dashboard", color = Surface1, fontSize = 14.sp)
                        }
                    }

                    TextButton(
                        onClick = onCancel,
                        enabled = !viewModel.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text("Kembali ke Beranda", color = TextSecondary, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    HaloMBGTheme {
        LoginScreen(
            onLoginSuccess = { _, _ -> },
            onCancel = {}
        )
    }
}
