package com.test.news.controller

import com.test.news.exception.ConflictException
import com.test.news.model.User
import com.test.news.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody user: User): User {
        val existingUser = this.userService.findUserByEmail(user.email)

        if (existingUser != null) {
            throw ConflictException()
        }

        return userService.create(user)
    }
}