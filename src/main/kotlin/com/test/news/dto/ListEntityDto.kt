package com.test.news.dto

data class ListEntityDto<T>(
    val items: List<T>,
    val currentPage: Int,
    val totalPages: Int
)