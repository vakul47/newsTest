package com.test.news.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.annotations.ApiModelProperty
import javax.persistence.*

@Entity
@Table(name = "articles")
data class Article(
        @ApiModelProperty(required = true)
        @Column(nullable = false)
        var title: String,

        @ApiModelProperty(required = true)
        @Column(length = 5000, nullable = false)
        var content: String,

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "user_id", nullable = false)
        @get:JsonProperty(access = JsonProperty.Access.READ_ONLY)
        @set:JsonIgnore
        var user: User,

        @OneToMany(
                mappedBy = "article",
                fetch = FetchType.EAGER,
                cascade = [CascadeType.ALL]
        )
        private val _comments: MutableList<Comment> = mutableListOf()
): BaseEntity()