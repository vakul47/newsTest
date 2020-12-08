package com.test.news.controller

import com.test.news.dto.ListEntityDto
import com.test.news.model.Comment
import com.test.news.service.CommentService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Order

import java.util.*

@RestController
@RequestMapping("/comments")
class CommentController(private val commentService: CommentService) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody comment: Comment): Comment = commentService.create(comment)

    @GetMapping
    fun list(
            @RequestParam(required = false) articleId: UUID?,
            @RequestParam(required = false) userId: UUID?,
            @RequestParam(defaultValue = "0") page: Int,
            @RequestParam(defaultValue = "10") size: Int,
            @RequestParam(defaultValue = "createdAt") sortBy: String,
            @RequestParam(defaultValue = "DESC") sortDirection: Sort.Direction
    ): ListEntityDto<Comment> {
        val order = Order(sortDirection, sortBy)
        val pageRequest: Pageable = PageRequest.of(page, size, Sort.by(order))

        val entityPage = commentService.getPage(pageRequest, articleId, userId)

        return ListEntityDto(
            items = entityPage.content,
            currentPage = entityPage.number,
            totalPages = entityPage.totalPages
        )
    }
}