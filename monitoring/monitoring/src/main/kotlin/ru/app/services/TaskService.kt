package ru.app.services

import org.springframework.stereotype.Service
import ru.app.dto.TaskDTO
import ru.app.model.Project
import ru.app.model.Task
import ru.app.model.User
import ru.app.repository.CompanyRepository
import ru.app.repository.ProjectRepository
import ru.app.repository.TaskRepository
import ru.app.repository.UserProjectRepository
import ru.app.repository.UserRepository

@Service
class TaskService(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository
) {
    fun getTasksById(userId: Long): List<Task> {
        return taskRepository.getAllById(userId)
    }

    fun getTasksByProject(projectId: Long?): List<Task> {
        return taskRepository.getAllForProject(projectId)
    }

    fun createTask(worker: User, creator: User, project: Project, taskInfo: TaskDTO) {
        taskRepository.createTask(
            worker.user_id!!,
            creator.user_id!!,
            project.project_id,
            taskInfo.task_name,
            taskInfo.task_description,
            taskInfo.start_time,
            taskInfo.end_time,
            taskInfo.status,
            taskInfo.progress
        )
    }
}