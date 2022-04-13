package ru.app.services

import org.springframework.stereotype.Service
import ru.app.repository.CompanyRepository
import ru.app.repository.ProjectRepository

@Service
class ProjectService(private val projectRepository: ProjectRepository) {
}