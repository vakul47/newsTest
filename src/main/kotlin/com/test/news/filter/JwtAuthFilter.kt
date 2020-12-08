package com.test.news.filter

import com.test.news.exception.InvalidTokenException
import com.test.news.provider.JwtTokenProvider
import io.jsonwebtoken.JwtException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.filter.OncePerRequestFilter
import java.lang.Exception
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.util.StringUtils

class JwtAuthFilter(
        private val jwtTokenProvider: JwtTokenProvider
): OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        try {
            val token = jwtTokenProvider.resolveToken(request)

            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                val authentication: Authentication = jwtTokenProvider.getAuthentication(token)
                SecurityContextHolder.getContext().authentication = authentication
            }

            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            if (e is JwtException) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, InvalidTokenException().message)
            }
            else{
                filterChain.doFilter(request, response)
            }
        }
    }
}