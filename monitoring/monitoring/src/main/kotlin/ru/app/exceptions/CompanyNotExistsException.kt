package ru.app.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class CompanyNotExistsException : RuntimeException("Company not exists")