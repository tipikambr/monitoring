package ru.app.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.app.dto.ProjectInfo
import ru.app.dto.TaskDTO
import ru.app.exceptions.BadRequestException
import ru.app.exceptions.ProjectNotExistsException
import ru.app.exceptions.TokenExpiredException
import ru.app.exceptions.UserNotFoundException
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

    @PostMapping("/api/v1/create/task")
    fun createTask(@RequestParam token: String, @RequestBody taskInfo: TaskDTO): Long? {
        log.info("POST: /api/v1/create/task")

        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)!!
        val project = projectService.getProjectByProjectNameAndCreatorLogin(taskInfo.project_name, taskInfo.creator_login!!) ?: throw ProjectNotExistsException()
        var task_id : Long? = null
        if (projectService.hasAccessToProject(me.user_id!!, project.project_id!!)) {
            val worker = if (taskInfo.worker_login != null)
                userService.getUser(taskInfo.worker_login) ?: throw UserNotFoundException()
                else me
            val creator = userService.getUser(taskInfo.creator_login) ?: throw UserNotFoundException()
            task_id = taskService.createTask(worker, creator, project, taskInfo)
        }

        return task_id
    }

    @PostMapping("/api/v1/delete/task")
    fun deleteTask(@RequestParam token: String, @RequestBody taskInfo: TaskDTO): String{
        log.info("POST: /api/v1/delete/task")

        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)!!
        val project = projectService.getProjectByProjectNameAndCreatorLogin(taskInfo.project_name, taskInfo.creator_login!!) ?: throw ProjectNotExistsException()
        if (projectService.hasAccessToProject(me.user_id!!, project.project_id!!)) {
            val worker = if (taskInfo.worker_login != null)
                userService.getUser(taskInfo.worker_login) ?: throw UserNotFoundException()
                else me
            val creator = userService.getUser(taskInfo.creator_login) ?: throw UserNotFoundException()
            taskService.deleteTask(worker, creator, project, taskInfo)
        }
        return "OK"
    }

    @PostMapping("/api/v1/update/task")
    fun updateTask(@RequestParam token: String, @RequestBody taskInfo: TaskDTO): Long? {
        log.info("POST: /api/v1/delete/task")

        val userId = tokenService.checkToken(token) ?: throw TokenExpiredException()
        val me = userService.getUser(userId)!!
        val project = projectService.getProjectByProjectNameAndCreatorLogin(taskInfo.project_name, taskInfo.creator_login!!) ?: throw ProjectNotExistsException()
        var id: Long? = null;
        if (projectService.hasAccessToProject(me.user_id!!, project.project_id!!)) {
            val worker = if (taskInfo.worker_login != null)
                userService.getUser(taskInfo.worker_login) ?: throw UserNotFoundException()
                else me
            val creator = userService.getUser(taskInfo.creator_login) ?: throw UserNotFoundException()
            id = taskService.updateTask(worker, creator, project, taskInfo)
        }
        return id
    }

    fun Task.toDTO(): TaskDTO {
        val creatorName = userService.getUser(creator_id)!!.login
        val projectName = projectService.getProjectById(project_id).project_name
        return TaskDTO(
            task_id,
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