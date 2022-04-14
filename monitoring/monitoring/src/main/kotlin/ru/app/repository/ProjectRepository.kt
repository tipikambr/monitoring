package ru.app.repository

import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import ru.app.model.Company
import ru.app.model.Project

interface ProjectRepository : CrudRepository<Project, String> {
    @Query("SELECT * FROM project")
    fun getAll(): List<Project>

    @Query("SELECT * FROM project WHERE project_id = :projectId LIMIT 1")
    fun getById(projectId: Long) : Project?

    @Modifying
    @Query("INSERT INTO project (project_name, project_description, project_creator_id) VALUES (:project_name, :project_description, :project_creator_id)")
    fun add (
        @Param("project_name") project_name: String,
        @Param("project_description") project_description: String?,
        @Param("project_creator_id") project_creator_id: Long
    )

    @Query("SELECT * FROM project WHERE project_name = :project_name AND project_creator_id = :project_creator_id LIMIT 1")
    fun getByNameAndCreator(
        @Param("project_name") project_name: String,
        @Param("project_creator_id") project_creator_id: Long
    ) : Project?
}