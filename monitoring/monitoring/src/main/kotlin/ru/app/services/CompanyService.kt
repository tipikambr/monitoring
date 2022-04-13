package ru.app.services

import org.springframework.stereotype.Service
import ru.app.exceptions.CompanyContainsWorkersException
import ru.app.exceptions.CompanyExistsException
import ru.app.exceptions.CompanyNotExistsException
import ru.app.exceptions.WorkersExistsException
import ru.app.model.Company
import ru.app.repository.CompanyRepository
import ru.app.repository.TokenRepository

@Service
class CompanyService(private val companyRepository: CompanyRepository) {
    fun getCompanyById(id: Int): Company {
        return companyRepository.getUserCompanyById(id) ?: throw CompanyNotExistsException()
    }

    fun getCompanyByName(name: String): Company {
        return companyRepository.getUserCompanyByName(name) ?: throw CompanyNotExistsException()
    }

    fun createCompany(company: Company) {
        val isExists = companyRepository.getUserCompanyByName(company.company_name)
        if (isExists != null) throw CompanyExistsException()
        companyRepository.save(company)
    }

    fun updateCompany(company: Company) {
        val base = companyRepository.getUserCompanyByName(company.company_name) ?: throw CompanyNotExistsException()

        companyRepository.updateCompany(
            base.company_id!!,
            base.company_name,
            company.company_description ?: base.company_description!!,
            company.finger_needed ?: base.finger_needed!!,
            company.photo_needed ?: base.photo_needed!!,
            company.manager_needed ?: base.manager_needed!!,
        )
    }

    fun deleteCompany(company: Company) {
        companyRepository.getUserCompanyByName(company.company_name) ?: throw CompanyNotExistsException()

        if (companyRepository.getCompanyWorkers(company.company_id!!).isNotEmpty())
            throw CompanyContainsWorkersException()
        companyRepository.delete(company)
    }

    fun getCompanies(): List<Company> {
        return companyRepository.getAll()
    }
}