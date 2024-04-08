package com.cilo.app.domain.home

import com.cilo.app.data.CompanyRepository
import com.cilo.app.data.models.CompanyGroup
import com.cilo.app.data.models.CompanyPublic
import org.mongodb.kbson.ObjectId

class CompanyUseCase(private val companyRepository: CompanyRepository) {
    fun getCompanyById(id: ObjectId): CompanyPublic = companyRepository.getCompanyById(id)
    fun getCompanyByCode(code: String): CompanyPublic = companyRepository.getCompanyByCode(code)

    fun getCompanyGroup(): List<CompanyGroup> = companyRepository.getCompanyGroup()

    fun getCompanyGroupById(id: ObjectId): List<CompanyGroup> = companyRepository.getCompanyGroupById(id)
    fun searchEmployee(searchTerm: String): List<CompanyGroup> = companyRepository.searchEmployee(searchTerm)
    fun searchTeam(searchTerm: String): List<CompanyGroup> = companyRepository.searchTeam(searchTerm)
}