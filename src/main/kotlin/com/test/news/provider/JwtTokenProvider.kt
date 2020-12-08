package com.test.news.provider

import com.test.news.service.UserService
import io.jsonwebtoken.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.*
import javax.servlet.http.HttpServletRequest

@Component
class JwtTokenProvider(private val userService: UserService) {
    @Value("\${app.secretKey}")
    private lateinit var secretKey: String

    @Value("\${app.tokenExpirationTime}")
    private var expirationTime: Long = 0

    fun createToken(email: String): String {
        val now = Date()
        val expiredAt = Date(now.time + this.expirationTime)

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiredAt)
                .signWith(SignatureAlgorithm.HS512, this.secretKey)
                .compact()
    }

    fun resolveToken(request: HttpServletRequest): String {
        val token = request.getHeader("Authorization")

        if (token != null && token.startsWith("Bearer ")) {
            return token.replace("Bearer ", "")
        }

        return ""
    }

    fun validateToken(token: String): Boolean {
        try {
            val claims = Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token)
            return !claims.body.expiration.before(Date())
        } catch (e: Throwable) {
            throw JwtException("Expired or invalid JWT token")
        }
    }

    fun getAuthentication(token: String): Authentication {
        val userDetails = this.userService.loadUserByUsername(this.getUsernameFromJwt(token))
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    private fun getUsernameFromJwt(token: String): String {
        return Jwts.parser()
                .setSigningKey(this.secretKey)
                .parseClaimsJws(token)
                .body
                .subject
    }
}