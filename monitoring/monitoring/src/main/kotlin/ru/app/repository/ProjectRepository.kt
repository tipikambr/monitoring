package ru.app.repository

import org.springframework.data.repository.CrudRepository
import ru.app.model.Project

interface ProjectRepository : CrudRepository<Project, String> {
}