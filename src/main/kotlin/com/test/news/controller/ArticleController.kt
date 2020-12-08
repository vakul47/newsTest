package com.test.news.controller

import com.test.news.dto.ListEntityDto
import com.test.news.exception.EntityNotFoundException
import com.test.news.exception.ForbiddenException
import com.test.news.model.Article
import com.test.news.service.ArticleService
import com.test.news.service.UserService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Order
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.*
import org.springframework.security.core.userdetails.User as SecurityUser


@RestController
@RequestMapping("/articles")
class ArticleController(
        private val articleService: ArticleService,
        private val userService: UserService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody article: Article, authentication: Authentication): Article {
        val securityUser: SecurityUser = authentication.principal as SecurityUser
        val userEntity = userService.findUserByEmail(securityUser.username)
        article.user = userEntity!!

        return articleService.create(article)
    }

    @GetMapping
    fun list(
            @RequestParam(required = false) title: String?,
            @RequestParam(defaultValue = "0") page: Int,
            @RequestParam(defaultValue = "10") size: Int,
            @RequestParam(defaultValue = "createdAt") sortBy: String,
            @RequestParam(defaultValue = "DESC") sortDirection: Sort.Direction
    ): ListEntityDto<Article>{
        val order = Order(sortDirection, sortBy)
        val pageRequest: Pageable = PageRequest.of(page, size, Sort.by(order))

        val entityPage = articleService.getPage(pageRequest, title)

        return ListEntityDto(
            items = entityPage.content,
            currentPage = entityPage.number,
            totalPages = entityPage.totalPages
        )
    }

    @GetMapping("{id}")
    fun item(@PathVariable id: UUID) = this.getArticle(id)

    @PutMapping("{id}")
    fun update(@PathVariable id: UUID, @RequestBody article: Article, authentication: Authentication): Article {
        val articleEntity = this.getArticle(id)
        val securityUser: SecurityUser = authentication.principal as SecurityUser
        val userEntity = userService.findUserByEmail(securityUser.username)

        if (articleEntity.user.id != userEntity?.id) {
            throw ForbiddenException()
        }

        return articleService.update(articleEntity, article)
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: UUID, authentication: Authentication){
        val articleEntity = this.getArticle(id)
        val securityUser: SecurityUser = authentication.principal as SecurityUser
        val userEntity = userService.findUserByEmail(securityUser.username)

        if (articleEntity.user.id != userEntity?.id) {
            throw ForbiddenException()
        }

        articleService.delete(articleEntity)
    }

    private fun getArticle(id: UUID): Article {
        val articleOptional: Optional<Article> = articleService.getOne(id)

        if (!articleOptional.isPresent) {
            throw EntityNotFoundException()
        }

        return articleOptional.get()
    }
}