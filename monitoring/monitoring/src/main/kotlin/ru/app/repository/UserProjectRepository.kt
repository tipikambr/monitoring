package ru.app.repository

import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import ru.app.model.Project
import ru.app.model.ProjectsUsers
import ru.app.model.Token

interface UserProjectRepository : CrudRepository<Token, String> {

    @Query("SELECT * FROM projects_users WHERE user_id = :user_id")
    fun getUserProjects(@Param("user_id") id: Long): List<ProjectsUsers>

    @Modifying
    @Query("INSERT INTO projects_users (project_id, user_id) VALUES (:project_id, :user_id)")
    fun addUserToProject(
        @Param("project_id") project_id: Long,
        @Param("user_id") user_id: Long
    )

    @Query("SELECT COUNT(*) FROM projects_users WHERE project_id = :project_id AND user_id = :user_id")
    fun isExists(
        @Param("project_id") project_id: Long,
        @Param("user_id") user_id: Long
    ): Int

}