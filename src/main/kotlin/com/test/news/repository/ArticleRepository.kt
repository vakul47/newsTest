package com.test.news.repository

import com.test.news.model.Article
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ArticleRepository: JpaRepository<Article, UUID> {
    fun findByTitleContaining(title: String, pageable: Pageable?): Page<Article>
}