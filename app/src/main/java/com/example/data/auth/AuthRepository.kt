package com.example.data.auth

import com.example.model.User
import com.example.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * BedSpace Authentication & User Session Manager
 * Manages registered users, authentication, roles, and user isolation.
 */
object AuthRepository {

    // Pre-registered users with required Admin account
    private val _registeredUsers = MutableStateFlow<List<User>>(
        listOf(
            User(
                id = "usr_admin_main",
                name = "Admin",
                email = "knchile@gmail.com",
                password = "Lusekelo@100",
                phone = "+260 97 000 0000",
                role = UserRole.ADMIN,
                isVerified = true
            ),
            User(
                id = "usr_student_1",
                name = "Thabo Musonda",
                email = "thabo@unza.zm",
                password = "Password123",
                phone = "+260 97 112 3344",
                role = UserRole.STUDENT,
                institution = "UNZA",
                studentId = "202308194",
                isVerified = true
            ),
            User(
                id = "usr_landlord_1",
                name = "Mr. Mwansa Tembo",
                email = "mwansa@tembo.zm",
                password = "Password123",
                phone = "+260 96 688 2244",
                role = UserRole.LANDLORD,
                nrcNumber = "194820/11/1",
                isVerified = true
            )
        )
    )
    val registeredUsers: StateFlow<List<User>> = _registeredUsers.asStateFlow()

    // Active logged-in user session (defaults to Student Thabo or Guest if not logged in)
    private val _currentUser = MutableStateFlow<User?>(
        // Default session initialized with active student for immediate testing, easily switched
        _registeredUsers.value[1]
    )
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun login(email: String, password: String):Result<User> {
        val cleanEmail = email.trim().lowercase()
        val user = _registeredUsers.value.find { 
            it.email.lowercase() == cleanEmail && it.password == password.trim() 
        }
        return if (user != null) {
            _currentUser.value = user
            Result.success(user)
        } else {
            Result.failure(Exception("Invalid email or password. Please check your credentials."))
        }
    }

    fun signUp(
        name: String,
        email: String,
        password: String,
        phone: String,
        role: UserRole,
        institution: String? = null,
        studentId: String? = null,
        nrcNumber: String? = null
    ): Result<User> {
        val cleanEmail = email.trim().lowercase()
        if (_registeredUsers.value.any { it.email.lowercase() == cleanEmail }) {
            return Result.failure(Exception("An account with this email already exists."))
        }

        val newUser = User(
            id = "usr_${UUID.randomUUID().toString().take(8)}",
            name = name.trim(),
            email = cleanEmail,
            password = password.trim(),
            phone = phone.trim(),
            role = role,
            institution = institution?.trim(),
            studentId = studentId?.trim(),
            nrcNumber = nrcNumber?.trim(),
            isVerified = role == UserRole.STUDENT // Students verified on signup, landlords undergo KYC review
        )

        _registeredUsers.value = _registeredUsers.value + newUser
        _currentUser.value = newUser
        return Result.success(newUser)
    }

    fun logout() {
        _currentUser.value = null
    }

    fun setCurrentUser(user: User) {
        _currentUser.value = user
    }
}
