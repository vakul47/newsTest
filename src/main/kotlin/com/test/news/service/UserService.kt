package com.test.news.service

import com.test.news.model.User
import com.test.news.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User as SecurityUser
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository): UserDetailsService {
    fun create(user: User): User {
        user.password = BCryptPasswordEncoder().encode(user.password)
        return userRepository.save(user)
    }

    fun findUserByEmail(email: String) = userRepository.findByEmail(email)

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByEmail(username)
        if (user !== null) {
            return SecurityUser(user.email, user.password, arrayListOf(SimpleGrantedAuthority("User")))
        } else {
            throw UsernameNotFoundException("username not found")
        }
    }
}