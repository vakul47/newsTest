package com.test.news.controller

import com.test.news.dto.LoginDto
import com.test.news.dto.TokenDto
import com.test.news.provider.JwtTokenProvider
import com.test.news.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
        private val userService: UserService,
        private val jwtTokenProvider: JwtTokenProvider
) {
    @Autowired
    var authenticationManager: AuthenticationManager? = null

    @PostMapping

    fun auth(@RequestBody loginDto: LoginDto): TokenDto {
        val userEntity = userService.findUserByEmail(loginDto.email)

        val isPassValid = BCryptPasswordEncoder().matches(loginDto.password, userEntity?.password)

        if (!isPassValid) {
            throw BadCredentialsException("Invalid username or password")
        }

        try {
            val username = loginDto.email
            authenticationManager?.authenticate(UsernamePasswordAuthenticationToken(username, userEntity?.password))
            val token = jwtTokenProvider.createToken(username)

            return TokenDto(token = token)
        } catch (e: AuthenticationException) {
            throw BadCredentialsException("Invalid username or password")
        }
    }
}