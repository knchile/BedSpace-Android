package com.example.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.auth.AuthRepository
import com.example.model.User
import com.example.model.UserRole
import com.example.ui.theme.Blue100
import com.example.ui.theme.Blue50
import com.example.ui.theme.Blue600
import com.example.ui.theme.Green100
import com.example.ui.theme.Green50
import com.example.ui.theme.Green700
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Red100
import com.example.ui.theme.Red50
import com.example.ui.theme.Red600
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.White

@Composable
fun AuthDialog(
    onDismiss: () -> Unit,
    onAuthSuccess: (User) -> Unit,
    initialTab: Int = 0 // 0 = Sign In, 1 = Sign Up
) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    
    // Login States
    var loginEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginPasswordVisible by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }

    // Signup States
    var signupName by remember { mutableStateOf("") }
    var signupEmail by remember { mutableStateOf("") }
    var signupPassword by remember { mutableStateOf("") }
    var signupPhone by remember { mutableStateOf("") }
    var signupRole by remember { mutableStateOf(UserRole.STUDENT) }
    var signupInstitution by remember { mutableStateOf("UNZA") }
    var signupNrc by remember { mutableStateOf("") }
    var signupError by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = White,
            border = BorderStroke(1.dp, Slate200),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Navy900),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("B", color = White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "BedSpaceZM Account",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Navy900
                                )
                            )
                            Text(
                                text = "Verified Student Housing Platform",
                                style = MaterialTheme.typography.labelSmall.copy(color = Slate500)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Close", tint = Slate500)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tabs: Sign In / Sign Up
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Slate50,
                    contentColor = Blue600,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { 
                            selectedTab = 0 
                            loginError = null
                        },
                        text = {
                            Text("Sign In", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium)
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { 
                            selectedTab = 1
                            signupError = null
                        },
                        text = {
                            Text("Create Account", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedTab == 0) {
                    // --- SIGN IN TAB ---
                    Text(
                        text = "Welcome back to BedSpaceZM",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        )
                    )
                    Text(
                        text = "Sign in to manage your bookings, properties, or moderation queue.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate600)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (loginError != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Red50,
                            border = BorderStroke(1.dp, Red100),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = loginError ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(color = Red600, fontWeight = FontWeight.Medium),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    OutlinedTextField(
                        value = loginEmail,
                        onValueChange = { 
                            loginEmail = it 
                            loginError = null
                        },
                        label = { Text("Email Address") },
                        placeholder = { Text("e.g. knchile@gmail.com") },
                        leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = Slate400) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_login_email_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = loginPassword,
                        onValueChange = { 
                            loginPassword = it 
                            loginError = null
                        },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = Slate400) },
                        trailingIcon = {
                            IconButton(onClick = { loginPasswordVisible = !loginPasswordVisible }) {
                                Icon(
                                    imageVector = if (loginPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = null,
                                    tint = Slate400
                                )
                            }
                        },
                        visualTransformation = if (loginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_login_password_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (loginEmail.isBlank() || loginPassword.isBlank()) {
                                loginError = "Please enter both your email and password."
                                return@Button
                            }
                            val res = AuthRepository.login(loginEmail, loginPassword)
                            res.onSuccess { user ->
                                onAuthSuccess(user)
                            }.onFailure { err ->
                                loginError = err.message ?: "Authentication failed."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Navy800),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("auth_login_submit_btn")
                    ) {
                        Text("Sign In", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = White))
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Slate200)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Quick Sign-In Presets:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Slate500)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Preset buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                loginEmail = "knchile@gmail.com"
                                loginPassword = "Lusekelo@100"
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                loginEmail = "thabo@unza.zm"
                                loginPassword = "Password123"
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Student", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                loginEmail = "mwansa@tembo.zm"
                                loginPassword = "Password123"
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Landlord", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                } else {
                    // --- SIGN UP TAB ---
                    Text(
                        text = "Create your BedSpaceZM Account",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        )
                    )
                    Text(
                        text = "Choose your role below to get personalized access.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate600)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Role Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (signupRole == UserRole.STUDENT) Blue50 else Slate50,
                            border = BorderStroke(1.dp, if (signupRole == UserRole.STUDENT) Blue600 else Slate200),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { signupRole = UserRole.STUDENT }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Filled.School, contentDescription = null, tint = if (signupRole == UserRole.STUDENT) Blue600 else Slate500, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Student", fontWeight = FontWeight.Bold, color = if (signupRole == UserRole.STUDENT) Blue600 else Slate700, fontSize = 12.sp)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (signupRole == UserRole.LANDLORD) Green50 else Slate50,
                            border = BorderStroke(1.dp, if (signupRole == UserRole.LANDLORD) Green700 else Slate200),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { signupRole = UserRole.LANDLORD }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Filled.Apartment, contentDescription = null, tint = if (signupRole == UserRole.LANDLORD) Green700 else Slate500, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Landlord", fontWeight = FontWeight.Bold, color = if (signupRole == UserRole.LANDLORD) Green700 else Slate700, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (signupError != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Red50,
                            border = BorderStroke(1.dp, Red100),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = signupError ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(color = Red600, fontWeight = FontWeight.Medium),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    OutlinedTextField(
                        value = signupName,
                        onValueChange = { signupName = it },
                        label = { Text(if (signupRole == UserRole.STUDENT) "Student Full Name" else "Landlord Full Name / Business") },
                        leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = Slate400) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = signupEmail,
                        onValueChange = { signupEmail = it },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = Slate400) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = signupPhone,
                        onValueChange = { signupPhone = it },
                        label = { Text("Zambian Phone / Mobile Money") },
                        placeholder = { Text("e.g. +260 97 123 4567") },
                        leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null, tint = Slate400) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (signupRole == UserRole.STUDENT) {
                        OutlinedTextField(
                            value = signupInstitution,
                            onValueChange = { signupInstitution = it },
                            label = { Text("University / College (e.g. UNZA, CBU, Mulungushi, Hone, Apex)") },
                            leadingIcon = { Icon(Icons.Filled.School, contentDescription = null, tint = Slate400) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    } else {
                        OutlinedTextField(
                            value = signupNrc,
                            onValueChange = { signupNrc = it },
                            label = { Text("National Registration Card (NRC Number)") },
                            placeholder = { Text("e.g. 194820/11/1") },
                            leadingIcon = { Icon(Icons.Filled.Shield, contentDescription = null, tint = Slate400) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    OutlinedTextField(
                        value = signupPassword,
                        onValueChange = { signupPassword = it },
                        label = { Text("Create Password") },
                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = Slate400) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (signupName.isBlank() || signupEmail.isBlank() || signupPassword.isBlank() || signupPhone.isBlank()) {
                                signupError = "Please fill in all required fields."
                                return@Button
                            }
                            val res = AuthRepository.signUp(
                                name = signupName,
                                email = signupEmail,
                                password = signupPassword,
                                phone = signupPhone,
                                role = signupRole,
                                institution = if (signupRole == UserRole.STUDENT) signupInstitution else null,
                                nrcNumber = if (signupRole == UserRole.LANDLORD) signupNrc else null
                            )
                            res.onSuccess { user ->
                                onAuthSuccess(user)
                            }.onFailure { err ->
                                signupError = err.message ?: "Sign up failed."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Navy800),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("auth_signup_submit_btn")
                    ) {
                        Text("Create Account", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = White))
                    }
                }
            }
        }
    }
}
