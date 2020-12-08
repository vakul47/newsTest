package com.test.news.service

import com.test.news.model.Comment
import com.test.news.repository.CommentRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class CommentService (private val commentRepository: CommentRepository) {
    fun getPage(pageRequest: Pageable, articleId: UUID?, userId: UUID?): Page<Comment> {
        if (articleId != null) {
            return this.commentRepository.findByArticleId(articleId, pageRequest)
        } else if(userId != null) {
            return this.commentRepository.findByUserId(userId, pageRequest)
        } else {
            return this.commentRepository.findAll(pageRequest)
        }
    }

    fun create(comment: Comment): Comment = commentRepository.save(comment)
}