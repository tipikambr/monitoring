package ru.app.repository

import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import ru.app.model.Company
import ru.app.model.User

interface CompanyRepository : CrudRepository<Company, String> {
    @Query("SELECT * FROM company WHERE company_name = :company_name LIMIT 1")
    fun getUserCompanyByName(@Param("company_name") name: String): Company?

    @Query("SELECT * FROM company WHERE company_id = :company_id LIMIT 1")
    fun getUserCompanyById(@Param("company_id") id: Int): Company?

    @Query("SELECT * FROM users WHERE company_id = :company_id")
    fun getCompanyWorkers(@Param("company_id") id: Int): List<User>

    @Query("SELECT * FROM company")
    fun getAll(): List<Company>

    @Modifying
    @Query("UPDATE company SET company_name = :company_name, company_description = :company_description, finger_needed = :finger_needed, photo_needed = :photo_needed, manager_needed = :manager_needed WHERE company_id = :company_id")
    fun updateCompany(
        @Param("company_id") company_id : Int,
        @Param("company_name") company_name : String,
        @Param("company_description") company_description : String,
        @Param("finger_needed") finger_needed : Boolean,
        @Param("photo_needed") photo_needed : Boolean,
        @Param("manager_needed") manager_needed : Boolean

    )
}