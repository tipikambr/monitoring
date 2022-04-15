package ru.app.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException
import ru.app.dto.ProjectInfo
import ru.app.dto.TaskDTO
import ru.app.exceptions.BadRequestException
import ru.app.exceptions.PermissionDeniedException
import ru.app.exceptions.ProjectNotExistsException
import ru.app.exceptions.TokenExpiredException
import ru.app.model.Task
import ru.app.services.ProjectService
import ru.app.services.TaskService
import ru.app.services.TokenService
import ru.app.services.UserService

@RestController
class TaskController(
    private val tokenService: TokenService,
    private val taskService: TaskService,
    private val userService: UserService,
    private val projectService: ProjectService
)  {
    private val log = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/api/v1/my/task")
    fun getMyTask(@RequestParam token: String): List<TaskDTO> {
        log.info("GET: /api/v1/my/task")

        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)!!

        return taskService.getTasksById(me.user_id!!).map{ it.toDTO() }
    }

    @PostMapping("/api/v1/project/task")
    fun getProjectTask(@RequestParam token: String, @RequestBody projectInfo: ProjectInfo): List<TaskDTO> {
        log.info("POST: /api/v1/project/task")

        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)!!
        if (projectInfo.project_name == null || projectInfo.project_creator_login == null) throw BadRequestException()
        val project = projectService.getProjectByProjectNameAndCreatorLogin(projectInfo.project_name, projectInfo.project_creator_login) ?: throw ProjectNotExistsException()
        return taskService.getTasksByProject(project.project_id).map{ it.toDTO() }
    }

//    @PostMapping("/api")

    fun Task.toDTO(): TaskDTO {
        val creatorName = userService.getUser(creator_id)!!.login
        val projectName = projectService.getProjectById(project_id).project_name
        return TaskDTO(
            creatorName,
            projectName,
            task_name,
            task_description,
            start_time,
            end_time,
            status,
            progress
        )
    }
}