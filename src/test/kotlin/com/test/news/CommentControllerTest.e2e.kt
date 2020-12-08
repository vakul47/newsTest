package com.test.news

import com.test.news.model.Article
import com.test.news.model.Comment
import com.test.news.model.User
import com.test.news.repository.ArticleRepository
import com.test.news.repository.CommentRepository
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CommentControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var articleRepository: ArticleRepository

    @Autowired
    lateinit var commentRepository: CommentRepository

    private lateinit var commentContent: String
    private val endPoint = "/comments"

    private lateinit var articleId: UUID
    private lateinit var userId: UUID

    @BeforeAll
    fun beforeAll() {
        val userEntity = this.userService.create(User("testComment@test.me", password = "123qweASD"))
        val userEntitySecond = this.userService.create(User("testComment2@test.me", password = "123qweASD"))

        val article = this.articleRepository.save(Article(
                title = "Comment Test",
                content = "Some content",
                user = userEntity
        ))

        val articleSecond = this.articleRepository.save(Article(
                title = "Comment Test List",
                content = "Some content",
                user = userEntitySecond
        ))

        this.commentContent = "{\"comment\": \"comment\", \"article\": { \"id\": \"${article.id}\" }}"
        this.articleId = articleSecond.id
        this.userId = userEntitySecond.id

        val commentList: MutableList<Comment> = mutableListOf()

        for (i in 1..10) {
            commentList.add(Comment(
                comment = "Filter Test ByArticle $i",
                article = articleSecond,
                user = userEntity
            ))
        }

        for (i in 1..5) {
            commentList.add(Comment(
                    comment = "Filter Test ByArticle $i",
                    article = article,
                    user = userEntitySecond
            ))
        }

        this.commentRepository.saveAll(commentList)
    }

    /***** TEST CREATE *****/

    @Test
    fun createCommentSucceeded(){
        this.mockMvc.perform(
            post(this.endPoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.commentContent)
            )
            .andExpect(MockMvcResultMatchers.status().isCreated)
    }

    /***** TEST LIST *****/

    @Test
    fun listCommentsWithFiltrationByArticleIdAndPagination(){
        this.mockMvc.perform(
            get(this.endPoint)
                    .param("articleId", "${this.articleId}")
                    .param("page", "2")
                    .param("size", "3")
            )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.items").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("$.currentPage").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").value(4))
    }

    @Test
    fun listCommentsWithFiltrationByUserId(){
        this.mockMvc.perform(
            get(this.endPoint)
                    .param("userId", "${this.userId}")
                    .param("size", "5")
            )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.items").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("$.currentPage").value(0))
            .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").value(1))
    }
}