package ru.app.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.CONFLICT)
class UserAccessException: RuntimeException("Login and password doesn't match")