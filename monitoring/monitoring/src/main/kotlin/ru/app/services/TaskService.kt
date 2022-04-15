package ru.app.services

import org.springframework.stereotype.Service
import ru.app.model.Project
import ru.app.model.Task
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
}