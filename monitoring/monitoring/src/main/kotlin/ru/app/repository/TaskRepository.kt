package ru.app.repository

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import ru.app.model.Task

interface TaskRepository : CrudRepository<Task, String> {
    @Query("SELECT * FROM task WHERE user_id = :user_id")
    fun getAllById( @Param("user_id") user_id: Long) : List<Task>

    @Query("SELECT * FROM task WHERE project_id = :project_id")
    fun getAllForProject(@Param("project_id") projectId: Long?): List<Task>
}