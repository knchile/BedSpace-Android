package com.example.data.auth

import com.example.model.User
import com.example.model.UserRole
import com.example.model.UserStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * BedSpace Authentication & User Session Manager
 * Manages registered users, authentication, roles, social sign-up, user isolation, and admin controls.
 */
object AuthRepository {

    // Pre-registered users with required Super Admin account
    private val _registeredUsers = MutableStateFlow<List<User>>(
        listOf(
            User(
                id = "usr_admin_main",
                name = "Admin",
                email = "knchile@gmail.com",
                password = "Lusekelo@100",
                phone = "+260 97 000 0000",
                role = UserRole.ADMIN,
                isVerified = true,
                status = UserStatus.ACTIVE
            ),
            User(
                id = "usr_student_1",
                name = "Thabo Musonda",
                email = "thabo@unza.zm",
                password = "Password123",
                phone = "+260 97 112 3344",
                role = UserRole.STUDENT,
                institution = "University of Zambia (UNZA)",
                studentId = "202308194",
                isVerified = true,
                status = UserStatus.ACTIVE
            ),
            User(
                id = "usr_landlord_1",
                name = "Mr. Mwansa Tembo",
                email = "mwansa@tembo.zm",
                password = "Password123",
                phone = "+260 96 688 2244",
                role = UserRole.LANDLORD,
                nrcNumber = "194820/11/1",
                isVerified = true,
                status = UserStatus.ACTIVE
            )
        )
    )
    val registeredUsers: StateFlow<List<User>> = _registeredUsers.asStateFlow()

    // Default session: null (Guest on Public Landing Page)
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun login(email: String, password: String): Result<User> {
        val cleanEmail = email.trim().lowercase()
        val user = _registeredUsers.value.find { 
            it.email.lowercase() == cleanEmail && it.password == password.trim() 
        }
        return if (user != null) {
            if (user.status == UserStatus.BANNED) {
                Result.failure(Exception("This account has been permanently BANNED for violations. Contact support@bedspace.zm"))
            } else if (user.status == UserStatus.BLOCKED) {
                Result.failure(Exception("Your account is currently BLOCKED (${user.blockReason ?: "Platform violation"}). Contact the Administrator."))
            } else {
                _currentUser.value = user
                Result.success(user)
            }
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
            isVerified = role == UserRole.STUDENT,
            status = UserStatus.ACTIVE
        )

        _registeredUsers.value = _registeredUsers.value + newUser
        _currentUser.value = newUser
        return Result.success(newUser)
    }

    fun socialSignUpOrLogin(
        provider: String,
        name: String,
        email: String,
        role: UserRole,
        institution: String? = null,
        phone: String = "+260 97 000 0000"
    ): Result<User> {
        val cleanEmail = email.trim().lowercase()
        val existingUser = _registeredUsers.value.find { it.email.lowercase() == cleanEmail }

        if (existingUser != null) {
            if (existingUser.status == UserStatus.BANNED) {
                return Result.failure(Exception("This account is BANNED from BedSpaceZM."))
            }
            if (existingUser.status == UserStatus.BLOCKED) {
                return Result.failure(Exception("This account is currently BLOCKED."))
            }
            _currentUser.value = existingUser
            return Result.success(existingUser)
        }

        val newUser = User(
            id = "usr_social_${UUID.randomUUID().toString().take(6)}",
            name = name.trim(),
            email = cleanEmail,
            password = "OAuth_${UUID.randomUUID().toString().take(8)}",
            phone = phone.trim(),
            role = role,
            institution = institution?.trim() ?: if (role == UserRole.STUDENT) "University of Zambia (UNZA)" else null,
            studentId = if (role == UserRole.STUDENT) "2026/S-${(100..999).random()}" else null,
            isVerified = true,
            status = UserStatus.ACTIVE,
            socialProvider = provider
        )

        _registeredUsers.value = _registeredUsers.value + newUser
        _currentUser.value = newUser
        return Result.success(newUser)
    }

    // Admin Moderation Controls: Block, Unblock, Ban, Delete User
    fun blockUser(userId: String, reason: String = "Violation of platform terms") {
        _registeredUsers.value = _registeredUsers.value.map { user ->
            if (user.id == userId) {
                user.copy(status = UserStatus.BLOCKED, blockReason = reason)
            } else user
        }
        if (_currentUser.value?.id == userId) {
            _currentUser.value = null // kick session
        }
    }

    fun unblockUser(userId: String) {
        _registeredUsers.value = _registeredUsers.value.map { user ->
            if (user.id == userId) {
                user.copy(status = UserStatus.ACTIVE, blockReason = null)
            } else user
        }
    }

    fun banUser(userId: String, reason: String = "Permanent ban by Super Admin") {
        _registeredUsers.value = _registeredUsers.value.map { user ->
            if (user.id == userId) {
                user.copy(status = UserStatus.BANNED, blockReason = reason)
            } else user
        }
        if (_currentUser.value?.id == userId) {
            _currentUser.value = null // kick session
        }
    }

    fun deleteUser(userId: String) {
        _registeredUsers.value = _registeredUsers.value.filter { it.id != userId }
        if (_currentUser.value?.id == userId) {
            _currentUser.value = null
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun setCurrentUser(user: User?) {
        _currentUser.value = user
    }
}

