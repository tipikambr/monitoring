package ru.app.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.app.exceptions.PermissionDeniedException
import ru.app.exceptions.TokenExpiredException
import ru.app.model.Company
import ru.app.model.Project
import ru.app.repository.ProjectRepository
import ru.app.repository.UserRepository
import ru.app.services.ProjectService
import ru.app.services.TokenService
import ru.app.services.UserService

@RestController
class ProjectController(
    private val tokenService: TokenService,
    private val projectService: ProjectService,
    private val userService: UserService
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/api/v1/all/project")
    fun getAll(@RequestParam token: String): List<Project> {
        log.info("GET: /api/v1/all/project")

        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)!!
        if (me.permissions != "admin") throw PermissionDeniedException()

        return projectService.getAllProjects()
    }

    @GetMapping("/api/v1/my/project")
    fun getMyProjects(@RequestParam token: String): List<Project> {
        log.info("GET: /api/v1/my/project")

        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)!!
        if (me.permissions != "admin") throw PermissionDeniedException()

        return projectService.getMyProjects(me)
    }

    @PostMapping("/api/v1/create/project")
    fun createProject(@RequestParam token: String, @RequestBody project: Project): String {
        log.info("POST: /api/v1/create/project")

        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)!!
        if (me.permissions != "admin" && me.permissions != "manager") throw PermissionDeniedException()

        projectService.createProject(project, me);
        return "OK"
    }
}