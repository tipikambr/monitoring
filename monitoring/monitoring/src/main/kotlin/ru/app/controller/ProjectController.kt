package ru.app.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.app.dto.ProjectDTO
import ru.app.dto.ProjectInfo
import ru.app.dto.UserProjectDTO
import ru.app.exceptions.PermissionDeniedException
import ru.app.exceptions.TokenExpiredException
import ru.app.model.Project
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
    fun getAll(@RequestParam token: String): List<ProjectInfo> {
        log.info("GET: /api/v1/all/project")

        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)!!
        if (me.permissions != "admin") throw PermissionDeniedException()

        return projectService.getAllProjects()
    }

    @GetMapping("/api/v1/my/project")
    fun getMyProjects(@RequestParam token: String): List<ProjectInfo> {
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

    // Я тут накосячил с правами по создателю, исправить!!! (78 -- мб project.project_creator_login)

    @PostMapping("/api/v1/update/project")
    fun updateProject(@RequestParam token: String, @RequestBody project: ProjectDTO): String {
        log.info("POST: /api/v1/update/project")

        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)!!
        if (me.permissions != "admin" && me.permissions != "manager") throw PermissionDeniedException()
        if (me.permissions != "admin" && project.project_creator_login != null)
            throw PermissionDeniedException()
        projectService.updateProject(project, me)
        return "OK"
    }

    @PostMapping("/api/v1/delete/project")
    fun deleteWorkers(@RequestParam token: String, @RequestParam soft: Boolean, @RequestBody project: ProjectDTO): String {
        log.info("POST: /api/v1/delete/project")

        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)!!
        if (me.permissions != "admin" && me.permissions != "manager") throw PermissionDeniedException()
        if (me.permissions != "admin" && project.project_creator_login != null)
            throw PermissionDeniedException()

        projectService.deleteProject(project, me, soft)
        return "OK"
    }

    @PostMapping("/api/v1/add_user/project")
    fun addWorkerToProject(@RequestParam token: String, @RequestBody userProject: UserProjectDTO): String {
        log.info("POST: /api/v1/add_user/project")

        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)!!
        if (me.permissions != "admin" && projectService.hasAccessToProject(userProject, me)) throw PermissionDeniedException()

        projectService.addUserToProject(userProject, userProject.project_creator_login ?: me.login)
        return "OK"
    }

    @PostMapping("/api/v1/remove_user/project")
    fun removeWorkerToProject(@RequestParam token: String, @RequestBody userProject: UserProjectDTO): String {
        log.info("POST: /api/v1/add_user/project")

        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)!!
        if (me.permissions != "admin" && projectService.hasAccessToProject(userProject, me)) throw PermissionDeniedException()

        projectService.removeUserToProject(userProject, userProject.project_creator_login ?: me.login)
        return "OK"
    }
}