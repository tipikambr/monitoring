package ru.app.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class ProjectAlreadyExistsException : RuntimeException("Project already exists") {
}

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class ProjectNotExistsException : RuntimeException("Project not exists") {
}