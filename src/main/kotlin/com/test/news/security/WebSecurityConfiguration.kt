package com.test.news.security

import com.test.news.filter.JwtAuthFilter
import com.test.news.provider.JwtTokenProvider
import com.test.news.service.UserService
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@EnableWebSecurity
class WebSecurityConfiguration(
        private val jwtTokenProvider: JwtTokenProvider,
        private val userService: UserService
): WebSecurityConfigurerAdapter() {
    @Bean
    fun jwtAuthFilter(): JwtAuthFilter {
        return JwtAuthFilter(jwtTokenProvider)
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userService).passwordEncoder(BCryptPasswordEncoder())
    }

    override fun configure(http: HttpSecurity) {
        http.httpBasic()
                .disable()
                .exceptionHandling().authenticationEntryPoint(JwtAuthEntryPoint()).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/articles").authenticated()
                .antMatchers(HttpMethod.PUT, "/articles/*").authenticated()
                .antMatchers(HttpMethod.DELETE, "/articles/*").authenticated()
                .and()
                .csrf().disable()
                .formLogin().disable()

        http.addFilterBefore(this.jwtAuthFilter(), UsernamePasswordAuthenticationFilter::class.java)
    }
}