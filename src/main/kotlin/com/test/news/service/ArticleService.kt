package com.test.news.service

import com.test.news.model.Article
import com.test.news.repository.ArticleRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class ArticleService (private val articleRepository: ArticleRepository) {
    fun getPage(pageRequest: Pageable, title: String?): Page<Article> {
        if (title !== null) {
            return this.articleRepository.findByTitleContaining(title, pageRequest)
        } else {
            return this.articleRepository.findAll(pageRequest)
        }
    }

    fun getOne(id: UUID): Optional<Article> = articleRepository.findById(id)

    fun create(article: Article): Article = articleRepository.save(article)

    fun update(oldArticle: Article, article: Article): Article {
        article.id = oldArticle.id
        article.createdAt = oldArticle.createdAt
        article.user = oldArticle.user

        return articleRepository.save(article)
    }

    fun delete(article: Article) = articleRepository.delete(article)
}