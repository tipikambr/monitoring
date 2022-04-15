package ru.app.repository

import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import ru.app.model.Task
import java.sql.Timestamp

interface TaskRepository : CrudRepository<Task, String> {
    @Query("SELECT * FROM task WHERE user_id = :user_id")
    fun getAllById( @Param("user_id") user_id: Long) : List<Task>

    @Query("SELECT * FROM task WHERE project_id = :project_id")
    fun getAllForProject(@Param("project_id") projectId: Long?): List<Task>

    @Modifying
    @Query("INSERT INTO task" +
            "(user_id, creator_id, project_id, task_name, task_description, start_time, end_time, status, progress) " +
            "VALUES" +
            "(:user_id, :creator_id, :project_id, :task_name, :task_description, :start_time, :end_time, :status, :progress)")
    fun createTask(
        @Param("user_id") user_id: Long,
        @Param("creator_id") creator_id: Long,
        @Param("project_id") project_id: Long?,
        @Param("task_name") task_name: String,
        @Param("task_description") task_description: String?,
        @Param("start_time") start_time: Timestamp?,
        @Param("end_time") end_time: Timestamp?,
        @Param("status") status: String?,
        @Param("progress") progress: String?
    )
}