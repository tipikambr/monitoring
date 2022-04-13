package ru.app.repository

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import ru.app.model.Company
import ru.app.model.User

interface CompanyRepository  : CrudRepository<Company, String> {
    @Query("SELECT * FROM company WHERE company_name = :company_name LIMIT 1")
    fun getUserCompanyByName(@Param("company_name") name: String): Company?

    @Query("SELECT * FROM company WHERE company_id = :company_id LIMIT 1")
    fun getUserCompanyById(@Param("company_id") id: Int): Company?

    fun save(company: Company)
}