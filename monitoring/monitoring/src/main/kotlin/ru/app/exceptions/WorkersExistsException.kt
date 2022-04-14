package ru.app.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.LOCKED)
class WorkersExistsException: RuntimeException("User has subordinates left")

@ResponseStatus(value = HttpStatus.LOCKED)
class CompanyContainsWorkersException: RuntimeException("Company contains workers")

@ResponseStatus(value = HttpStatus.LOCKED)
class ProjectContainsWorkersException: RuntimeException("Project contains workers")