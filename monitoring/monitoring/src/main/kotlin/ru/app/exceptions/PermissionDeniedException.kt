package ru.app.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.LOCKED)
class PermissionDeniedException : RuntimeException("Permission denied")