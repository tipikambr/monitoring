package ru.app.services

import org.springframework.stereotype.Service
import ru.app.exceptions.ProjectAlreadyExistsException
import ru.app.model.Company
import ru.app.model.Project
import ru.app.model.User
import ru.app.repository.CompanyRepository
import ru.app.repository.ProjectRepository
import ru.app.repository.UserProjectRepository

@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val userProjectRepository: UserProjectRepository
) {

    fun getAllProjects(): List<Project> {
        return projectRepository.getAll()
    }

    fun getMyProjects(user: User): List<Project> {
        return userProjectRepository
            .getUserProjects(user.user_id!!)
            .mapNotNull { projectRepository.getById(it.project_id) }
    }

    fun createProject(project: Project, user: User) {
        var interest = projectRepository.getByNameAndCreator(project.project_name, user.user_id!!)
        if (interest != null) throw ProjectAlreadyExistsException()
        projectRepository.add(project.project_name, project.project_description, user.user_id)
        interest = projectRepository.getByNameAndCreator(project.project_name, user.user_id)
        userProjectRepository.addUserToProject(interest!!.project_id, user.user_id)
    }
}