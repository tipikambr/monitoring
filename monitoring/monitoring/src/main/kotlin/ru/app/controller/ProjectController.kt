package ru.app.controller

import org.springframework.web.bind.annotation.RestController
import ru.app.repository.ProjectRepository
import ru.app.services.TokenService

@RestController
class ProjectController(
    private val tokenService: TokenService,
    private val projectRepository: ProjectRepository
) {
}