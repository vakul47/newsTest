package com.test.news

import com.test.news.model.Article
import com.test.news.model.User
import com.test.news.provider.JwtTokenProvider
import com.test.news.repository.ArticleRepository
import com.test.news.service.UserService
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*


@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ArticleControllerTest {
	@Autowired
	lateinit var mockMvc: MockMvc

	@Autowired
	lateinit var userService: UserService

	@Autowired
	lateinit var articleRepository: ArticleRepository

	@Autowired
	var authenticationManager: AuthenticationManager? = null

	@Autowired
	var jwtTokenProvider: JwtTokenProvider? = null

	private lateinit var authToken: String
	private lateinit var authTokenSecond: String
	private lateinit var articleId: UUID
	private val articleContent = "{\"title\": \"title\", \"content\": \"content\"}"
	private val articleUpdate = "{\"title\": \"title Updated\", \"content\": \"content Updated\"}"
	private val endPoint = "/articles"
	private lateinit var endPointItem: String
	private lateinit var endPointUpdate: String
	private lateinit var endPointDelete: String

	@BeforeAll
	fun beforeAll() {
		val email = "test@test.me"
		val userEntity = this.userService.create(User(email, password = "123qweASD"))
		authenticationManager?.authenticate(UsernamePasswordAuthenticationToken(email, userEntity.password))
		this.authToken = jwtTokenProvider?.createToken(email)!!

		val emailSecond = "testSecond@test.me"
		val userEntitySecond = this.userService.create(User(emailSecond, password = "123qweASD"))
		authenticationManager?.authenticate(UsernamePasswordAuthenticationToken(emailSecond, userEntitySecond.password))
		this.authTokenSecond = jwtTokenProvider?.createToken(emailSecond)!!

		val articleList: MutableList<Article> = mutableListOf()

		for (i in 1..10) {
			articleList.add(Article(
					title = "Filter Test $i",
					content = "Some content $i",
					user = userEntity
			))
		}

		val updateArticle = this.articleRepository.save(Article(
				title = "Update Test",
				content = "Some content",
				user = userEntity
		))

		val deleteArticle = this.articleRepository.save(Article(
				title = "Delete Test",
				content = "Some content",
				user = userEntity
		))

		val itemArticle = this.articleRepository.save(Article(
				title = "Item Test",
				content = "Some content",
				user = userEntity
		))

		this.articleId = updateArticle.id
		this.endPointUpdate = this.endPoint + "/${updateArticle.id}"
		this.endPointDelete = this.endPoint + "/${deleteArticle.id}"
		this.endPointItem = this.endPoint + "/${itemArticle.id}"

		this.articleRepository.saveAll(articleList)
	}

	/***** TEST CREATE *****/

	@Test
	fun createArticleSucceeded(){
		this.mockMvc.perform(
				post(this.endPoint)
					.contentType(MediaType.APPLICATION_JSON)
					.content(this.articleContent)
					.header("Authorization", "Bearer ${this.authToken}")
			)
			.andExpect(status().isCreated)
	}

	@Test
	fun createArticleUnauth(){
		this.mockMvc.perform(
				post(this.endPoint)
					.contentType(MediaType.APPLICATION_JSON)
					.content(this.articleContent)
			).andExpect(status().isUnauthorized)
	}

	/***** TEST LIST *****/

	@Test
	fun listArticlesWithFiltrationAndPagination(){
		this.mockMvc.perform(
				get(this.endPoint)
					.param("title", "Filter")
					.param("page", "3")
					.param("size", "2")
			)
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.items").isArray)
			.andExpect(jsonPath("$.currentPage").value(3))
			.andExpect(jsonPath("$.totalPages").value(5))
	}

	/***** TEST ITEM *****/

	@Test
	fun itemArticleSucceeded(){
		this.mockMvc.perform(get(this.endPointItem))
				.andExpect(status().isOk)
	}

	/***** TEST UPDATE *****/

	@Test
	fun updateArticleUnauth(){
		this.mockMvc.perform(
				put(this.endPointUpdate)
						.contentType(MediaType.APPLICATION_JSON)
						.content(this.articleUpdate)
			).andExpect(status().isUnauthorized)
	}

	@Test
	fun updateArticleForbidden(){
		this.mockMvc.perform(
				put(this.endPointUpdate)
						.contentType(MediaType.APPLICATION_JSON)
						.content(this.articleUpdate)
						.header("Authorization", "Bearer ${this.authTokenSecond}")
			).andExpect(status().isForbidden)
	}

	@Test
	fun updateArticleSucceeded(){
		this.mockMvc.perform(
				put(this.endPointUpdate)
						.contentType(MediaType.APPLICATION_JSON)
						.content(this.articleUpdate)
						.header("Authorization", "Bearer ${this.authToken}")
			)
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.title").value("title Updated"))
			.andExpect(jsonPath("$.content").value("content Updated"))
	}

	/***** TEST DELETE *****/

	@Test
	fun deleteArticleUnauth(){
		this.mockMvc.perform(delete(this.endPointDelete))
				.andExpect(status().isUnauthorized)
	}

	@Test
	fun deleteArticleForbidden(){
		this.mockMvc.perform(
				delete(this.endPointDelete)
						.header("Authorization", "Bearer ${this.authTokenSecond}")
			).andExpect(status().isForbidden)
	}

	@Test
	fun deleteArticleSucceeded(){
		this.mockMvc.perform(
				delete(this.endPointDelete)
						.header("Authorization", "Bearer ${this.authToken}")
				)
				.andExpect(status().isNoContent)
	}
}
