package ru.app.services

import org.springframework.stereotype.Service
import ru.app.dto.ProjectDTO
import ru.app.dto.ProjectInfo
import ru.app.exceptions.ProjectAlreadyExistsException
import ru.app.exceptions.ProjectContainsWorkersException
import ru.app.exceptions.ProjectNotExistsException
import ru.app.exceptions.UserFromAnotherCompanyException
import ru.app.exceptions.UserNotFoundException
import ru.app.model.Project
import ru.app.model.ProjectsUsers
import ru.app.model.User
import ru.app.repository.CompanyRepository
import ru.app.repository.ProjectRepository
import ru.app.repository.UserProjectRepository
import ru.app.repository.UserRepository

@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val userProjectRepository: UserProjectRepository,
    private val userRepository: UserRepository,
    private val companyRepository: CompanyRepository
) {

    fun getAllProjects(): List<ProjectInfo> {
        return projectRepository.getAll().map {
            ProjectInfo(
                it.project_name,
                it.project_description,
                userRepository.getUser(it.project_creator_id!!)!!.login,
                companyRepository.getUserCompanyById(it.company_id!!)!!.company_name
            )
        }
    }

    fun getMyProjects(user: User): List<ProjectInfo> {
        return userProjectRepository
            .getUserProjects(user.user_id!!)
            .mapNotNull {
                val project = projectRepository.getById(it.project_id)
                if (project == null) project else ProjectInfo(
                    project.project_name,
                    project.project_description,
                    userRepository.getUser(project.project_creator_id!!)!!.login,
                    companyRepository.getUserCompanyById(project.company_id!!)!!.company_name
                )
            }
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
        // Текущий создатель проекта
        val projectCreator = if (project.old_project_creator_login != null)
            userRepository.getUser(project.old_project_creator_login)?.user_id ?: throw UserNotFoundException()
        else
            user.user_id

        //Проект, который мы хотим менять
        val interest: Project? = projectRepository.getByNameAndCreator(project.old_project_name!!, projectCreator!!)
            ?: throw ProjectNotExistsException()

        //Новый создатель проекта (главный), если не меняется, то он равен null
        val newCreator = if (project.project_creator_login != null) userRepository.getUser(project.project_creator_login) else null

        //Если новый создатель проекта из другой компании, то ошибка
        if (newCreator != null && newCreator.company_id != user.company_id)
            throw UserFromAnotherCompanyException()

        //Если у нового создателя уже есть проект с таким названием, то выкинет ошибку, что такой проект уже существует
        if (newCreator != null) {
            val isProjectAlreadyExists = userProjectRepository.getUserProjects(newCreator.user_id!!)
                .mapNotNull {
                    val pr = projectRepository.getById(it.project_id)
                    pr?.project_name
                }.contains(project.project_name)
            if (isProjectAlreadyExists) throw ProjectAlreadyExistsException()
        }

        // Обновление проекта
        projectRepository.update(
            interest!!.project_id,
            project.project_name ?: interest.project_name,
            project.project_description ?: interest.project_description,
            if (newCreator != null) newCreator.user_id else interest.project_creator_id
        )

        // Если новый создатель еще не в проекте, то добавляем его
        if (newCreator != null && userProjectRepository.isExists(interest!!.project_id!!, newCreator.user_id!!) == 0)
            userProjectRepository.addUserToProject(interest.project_id!!, newCreator.user_id!!)
    }




    fun deleteProject(project: ProjectDTO, user: User, soft: Boolean) {
        // Текущий создатель проекта
        val projectCreator = if (project.project_creator_login != null)
            userRepository.getUser(project.project_creator_login)?.user_id ?: throw UserNotFoundException()
        else
            user.user_id

        // Проект, который хотим удалить
        var interest: Project? = projectRepository.getByNameAndCreator(project.project_name!!, projectCreator!!)
            ?: throw ProjectNotExistsException()

        //Удаление проекта, если в проекте всего один человек, удаляется без проблем, если больше -- то нужен параметр hard delete.
        if (userProjectRepository.getProjectUsers(interest!!.project_id!!).size <= 1) {
            userProjectRepository.deleteByProjectId(interest.project_id!!)
            projectRepository.delete(interest!!)
        } else {
            if (soft) throw ProjectContainsWorkersException()
            userProjectRepository.deleteByProjectId(interest.project_id!!)
            projectRepository.delete(interest!!)
        }
    }
}