package ru.app.services

import org.springframework.stereotype.Service
import ru.app.exceptions.ActivityAlreadyFinishedException
import ru.app.exceptions.ActivityNotEndedException
import ru.app.exceptions.ActivityNotExistsException
import ru.app.model.Activity
import ru.app.model.User
import ru.app.repository.ActivityRepository
import java.sql.Timestamp
import java.util.Date


@Service
class ActivityService(
    private val activityRepository: ActivityRepository,
) {
    fun getUserActivity(user: User): List<Activity> {
        return activityRepository.getAll(user.user_id)
    }

    fun getTeamActivity(team : List<User>): List<Activity> {
        val result = mutableListOf<Activity>()
        for (userActivity in team.map { getUserActivity(it) } )
            result.addAll(userActivity)
        return result
    }

    fun startActivity(user: User): Long {
        if (getUserActivity(user).find{ it.end_time == null } != null) throw ActivityNotEndedException()
        return activityRepository.startActivity(user.user_id!!, Timestamp(Date().time))
    }

    fun endActivity(user: User) {
        val activity = getUserActivity(user).find{it.end_time == null} ?: throw ActivityAlreadyFinishedException()
        activityRepository.endActivity(activity.activity_id, user.user_id!!, Timestamp(Date().time))
    }
}