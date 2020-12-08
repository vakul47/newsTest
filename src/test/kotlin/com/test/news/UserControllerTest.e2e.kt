package com.test.news

import com.test.news.model.User
import com.test.news.service.UserService
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var userService: UserService

    private val conflictEmail = "testConflict@test.me"
    private val endPoint = "/users"

    @BeforeAll
    fun beforeAll() {
        this.userService.create(User("testConflict@test.me", password = "123qweASD"))
    }

    /***** TEST CREATE *****/

    @Test
    fun createUserSucceeded(){
        val content = "{\"email\": \"createUser@test.me\", \"password\": \"123qweASD\"}"

        this.mockMvc.perform(
            MockMvcRequestBuilders.post(this.endPoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
            )
            .andExpect(MockMvcResultMatchers.status().isCreated)
    }

    @Test
    fun createUserConflict(){
        val content = "{\"email\": \"${this.conflictEmail}\", \"password\": \"123qweASD\"}"

        this.mockMvc.perform(
            MockMvcRequestBuilders.post(this.endPoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
            )
            .andExpect(MockMvcResultMatchers.status().isConflict)
    }
}