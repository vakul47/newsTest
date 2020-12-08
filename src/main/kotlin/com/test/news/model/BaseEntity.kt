package com.test.news.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.annotations.ApiModelProperty
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.util.*
import javax.persistence.*


@MappedSuperclass
abstract class BaseEntity{
    @ApiModelProperty(readOnly = true)
    @Id
    @GeneratedValue
    var id: UUID = UUID.randomUUID()

    @ApiModelProperty(readOnly = true)
    @CreatedDate
    @JsonProperty("createdAt")
    @Column(updatable = false, nullable = false)
    lateinit var createdAt: Date

    @ApiModelProperty(readOnly = true)
    @LastModifiedDate
    @JsonProperty("modifiedAt")
    @Column(nullable = false)
    lateinit var modifiedAt: Date

    @PrePersist
    fun prePersist() {
        createdAt = Date()
        modifiedAt = Date()
    }

    @PreUpdate
    fun preUpdate() {
        modifiedAt = Date()
    }
}