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

    @Query("INSERT INTO task" +
            "(user_id, creator_id, project_id, task_name, task_description, start_time, end_time, status, progress) " +
            "VALUES" +
            "(:user_id, :creator_id, :project_id, :task_name, :task_description, :start_time, :end_time, :status, :progress) returning task_id")
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
    ): Long?


    @Query("SELECT * FROM task WHERE task_id = :task_id AND creator_id = :user_id")
    fun findTask(@Param("user_id") user_id: Long, @Param("task_id") task_id: Long) : Task?


//    @Query("UPDATE project SET project_name = :project_name, project_description =
    //    :project_description, project_creator_id = :project_creator_id
    //    WHERE project_id = :project_id")


    @Modifying
    @Query("UPDATE task " +
            "SET user_id = :user_id, project_id = :project_id, task_name = :task_name, " +
            "task_description = :task_description, start_time = :start_time, end_time = :end_time, " +
            "status = :status, progress = :progress " +
            "WHERE creator_id = :creator_id AND task_id = :task_id")
    fun updateTask(
        @Param("task_id") taskId: Long,
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

    @Modifying
    @Query("DELETE FROM task " +
            "WHERE project_id = :project_id")
    fun deleteByPtojectId(  @Param("project_id") project_id: Long)
}