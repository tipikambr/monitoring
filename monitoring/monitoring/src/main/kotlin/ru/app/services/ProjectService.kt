package ru.app.services

import org.springframework.stereotype.Service
import ru.app.dto.ProjectDTO
import ru.app.dto.ProjectInfo
import ru.app.dto.UserDTO
import ru.app.dto.UserProjectDTO
import ru.app.exceptions.ProjectAlreadyExistsException
import ru.app.exceptions.ProjectContainsWorkersException
import ru.app.exceptions.ProjectNotExistsException
import ru.app.exceptions.UserAlreadyExistsException
import ru.app.exceptions.UserFromAnotherCompanyException
import ru.app.exceptions.UserNotFoundException
import ru.app.model.Project
import ru.app.model.User
import ru.app.repository.ActivityRepository
import ru.app.repository.CompanyRepository
import ru.app.repository.GeolocationRepository
import ru.app.repository.ProjectRepository
import ru.app.repository.UserProjectRepository
import ru.app.repository.UserRepository

@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val userProjectRepository: UserProjectRepository,
    private val userRepository: UserRepository,
    private val companyRepository: CompanyRepository,
    private val activityRepository: ActivityRepository,
    private val geolocationRepository: GeolocationRepository
) {

    fun getProjectById(projectId: Long): Project {
        return projectRepository.getById(projectId) ?: throw ProjectNotExistsException()
    }

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
        var interest = projectRepository.getByNameAndCreator(project.project_name)
        if (interest != null) throw ProjectAlreadyExistsException()
        projectRepository.add(project.project_name, user.company_id!!, project.project_description, user.user_id!!)
        interest = projectRepository.getByNameAndCreator(project.project_name)

        if (userProjectRepository.isExists(interest!!.project_id!!, user.user_id!!) == 0)
            userProjectRepository.addUserToProject(interest!!.project_id!!, user.user_id!!)
    }

    fun updateProject(project: ProjectDTO, user: User) {
        // Текущий создатель проекта
        val projectCreator = if (project.old_project_creator_login != null)
            userRepository.getUser(project.old_project_creator_login)?.user_id ?: throw UserNotFoundException()
        else
            user.user_id

        //Проект, который мы хотим менять
        val interest: Project? = projectRepository.getByNameAndCreator(project.old_project_name!!)
            ?: throw ProjectNotExistsException()

        //Новый создатель проекта (главный), если не меняется, то он равен null
        val newCreator = if (project.project_creator_login != null &&
            project.old_project_creator_login != project.project_creator_login)
                userRepository.getUser(project.project_creator_login) else null

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
        var interest: Project? = projectRepository.getByNameAndCreator(project.project_name!!)
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

    // Если пользователь не является создателем проекта, то у него нет доступа к редактированию проекта. (если он не админ)
    fun canEditProject(userProjectName: String, user: User): Boolean {
        val project = projectRepository.getByNameAndCreator(userProjectName)
        return project != null
    }

    fun hasAccessToProject(userId: Long, projectId: Long): Boolean {
        return userProjectRepository.isExists(projectId, userId) != 0
    }

    fun addUserToProject(userProject: UserProjectDTO, creator_login: String) {
        val creator = userRepository.getUser(creator_login) ?: throw UserNotFoundException()
        val project = projectRepository.getByNameAndCreator(userProject.project_name) ?: throw ProjectNotExistsException()
        val user = userRepository.getUser(userProject.user_login) ?: throw UserNotFoundException()

        if (creator.company_id != user.company_id) throw UserFromAnotherCompanyException()

        if (userProjectRepository.isExists(project.project_id!!, user.user_id!!) != 0) throw UserAlreadyExistsException()

        userProjectRepository.addUserToProject(project.project_id!!, user.user_id!!)
    }

    fun removeUserToProject(userProject: UserProjectDTO, creator_login: String) {
        val creator = userRepository.getUser(creator_login) ?: throw UserNotFoundException()
        val project = projectRepository.getByNameAndCreator(userProject.project_name) ?: throw ProjectNotExistsException()
        val user = userRepository.getUser(userProject.user_login) ?: throw UserNotFoundException()

        if (creator.company_id != user.company_id) throw UserFromAnotherCompanyException()

        if (userProjectRepository.isExists(project.project_id!!, user.user_id!!) == 0) throw UserNotFoundException()

        userProjectRepository.delete(user.user_id, project.project_id)
    }

    fun getProjectWorkers(projectName: String, creatorLogin: String): List<UserDTO> {
        val creator = userRepository.getUser(creatorLogin) ?: throw UserNotFoundException()
        val project = projectRepository.getByNameAndCreator(projectName)

        return userProjectRepository.getProjectUsers(project!!.project_id!!).map{
            val user = userRepository.getUser(it.user_id)
            val companyName = companyRepository.getUserCompanyById(user!!.company_id!!)!!.company_name
            val bossLogin = if (user.boss_id == null || user.boss_id == 0L) null else userRepository.getUser(user.boss_id)!!.login

            val lastActivity = activityRepository.getAll(user.user_id).lastOrNull()
            val lastGeolocation = geolocationRepository.getUserGeolocation(user.user_id!!).lastOrNull()?.timeUpdate

            UserDTO(
                user.user_name,
                user.login,
                null,
                companyName,
                user.hours,
                user.permissions,
                bossLogin,
                lastActivity != null && lastActivity.end_time == null,
                lastGeolocation
            )
        }
    }

    fun getProjectByProjectNameAndCreatorLogin(projectName: String): Project? {
//        val creator = userRepository.getUser(projectCreatorLogin) ?: throw UserNotFoundException()
        return projectRepository.getByNameAndCreator(projectName)
    }
}