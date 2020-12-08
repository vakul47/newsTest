package com.test.news.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
        @Column(unique = true, nullable = false)
        val email: String,

        @get:JsonIgnore
        @set:JsonProperty
        var password: String,

        @OneToMany(
                mappedBy = "user",
                fetch = FetchType.EAGER,
                cascade = [CascadeType.ALL]
        )
        private val _articles: MutableList<Article> = mutableListOf(),

        @OneToMany(
                mappedBy = "user",
                fetch = FetchType.EAGER,
                cascade = [CascadeType.ALL]
        )
        private val _comments: MutableList<Comment> = mutableListOf()
): BaseEntity()