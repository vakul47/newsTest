package com.test.news.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Conflict")
class ConflictException: RuntimeException()