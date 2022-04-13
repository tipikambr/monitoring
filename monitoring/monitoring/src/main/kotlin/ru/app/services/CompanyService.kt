package ru.app.services

import org.springframework.stereotype.Service
import ru.app.exceptions.CompanyExistsException
import ru.app.exceptions.CompanyNotExistsException
import ru.app.model.Company
import ru.app.repository.CompanyRepository
import ru.app.repository.TokenRepository

@Service
class CompanyService(private val companyRepository: CompanyRepository) {
    fun getCompanyById(id: Int): Company {
        return companyRepository.getUserCompanyById(id) ?: throw CompanyNotExistsException()
    }

    fun createCompany(company: Company) {
        val isExists = companyRepository.getUserCompanyByName(company.company_name)
        if (isExists != null) throw CompanyExistsException()
        companyRepository.save(company)
    }
}