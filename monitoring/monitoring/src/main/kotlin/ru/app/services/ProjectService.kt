package ru.app.services

import org.springframework.stereotype.Service
import ru.app.dto.ProjectDTO
import ru.app.exceptions.ProjectAlreadyExistsException
import ru.app.exceptions.ProjectNotExistsException
import ru.app.exceptions.UserNotFoundException
import ru.app.model.Project
import ru.app.model.User
import ru.app.repository.ProjectRepository
import ru.app.repository.UserProjectRepository
import ru.app.repository.UserRepository

@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val userProjectRepository: UserProjectRepository,
    private val userRepository: UserRepository
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
        projectRepository.add(project.project_name, user.company_id!!, project.project_description, user.user_id)
        interest = projectRepository.getByNameAndCreator(project.project_name, user.user_id)

        if (userProjectRepository.isExists(interest!!.project_id!!, user.user_id) == 0)
            userProjectRepository.addUserToProject(interest!!.project_id!!, user.user_id)
    }

    fun updateProject(project: ProjectDTO, user: User) {
        val projectCreator = if (project.old_project_creator_login != null)
            userRepository.getUser(project.old_project_creator_login)?.user_id ?: throw UserNotFoundException()
        else
            user.user_id

        var interest: Project? = projectRepository.getByNameAndCreator(project.old_project_name, projectCreator!!)
            ?: throw ProjectNotExistsException()

        val newCreator = if (project.project_creator_login != null) userRepository.getUser(project.project_creator_login) else null

        projectRepository.update(
            interest!!.project_id,
            project.project_name ?: interest.project_name,
            project.project_description ?: interest.project_description,
            if (newCreator != null) newCreator.user_id else interest.project_creator_id

        )

        if (newCreator != null && userProjectRepository.isExists(interest!!.project_id!!, newCreator.user_id!!) == 0)
            userProjectRepository.addUserToProject(interest.project_id!!, newCreator.user_id!!)
    }
}