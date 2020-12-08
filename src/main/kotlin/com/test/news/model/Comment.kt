package com.test.news.model

import javax.persistence.*

@Entity
@Table(name = "comments")
data class Comment(
        var comment: String,

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "article_id", nullable = false)
        val article: Article,

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "user_id")
        var user: User
): BaseEntity()