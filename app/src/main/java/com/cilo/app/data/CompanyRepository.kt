package com.cilo.app.data

import com.cilo.app.data.models.CompanyGroup
import com.cilo.app.data.models.CompanyPublic
import com.cilo.app.data.network.RealmAPI
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import org.mongodb.kbson.ObjectId

class CompanyRepository(private val realm: Realm, private val realmAPI: RealmAPI) {
    fun getCompanyById(id: ObjectId): CompanyPublic = CompanyPublic().apply {
        val company = realm.query<CompanyPublic>("_id == $0", id).first().find()
        name = company?.name
        capacity = company?.capacity
        code = company?.code
        competitionEndDate = company?.competitionEndDate
    }

    fun getCompanyByCode(code: String): CompanyPublic = CompanyPublic().apply {
        val company = realm.query<CompanyPublic>("code == $0", code).first().find()
        name = company?.name
        capacity = company?.capacity
        this.code = company?.code
        competitionEndDate = company?.competitionEndDate
    }

    fun getCompanyGroup(): List<CompanyGroup> =
        realm.query<CompanyGroup>().find().map {
            CompanyGroup().apply {
                category = it.category
                employees = it.employees
                groupProfileId = it.groupProfileId
                name = it.name
                parentGroupIds = it.parentGroupIds
            }
        }

    fun getCompanyGroupById(id: ObjectId): List<CompanyGroup> =
        realm.query<CompanyGroup>("_partition CONTAINS[c] $0", id.toHexString()).find().map {
            CompanyGroup().apply {
                category = it.category
                employees = it.employees
                groupProfileId = it.groupProfileId
                name = it.name
                parentGroupIds = it.parentGroupIds
            }
        }

    fun searchEmployee(searchTerm: String): List<CompanyGroup> =
        realm.query<CompanyGroup>("employees.name CONTAINS[c] $0", searchTerm).find().map {
            CompanyGroup().apply {
                category = it.category
                employees = it.employees
                groupProfileId = it.groupProfileId
                name = it.name
                parentGroupIds = it.parentGroupIds
            }
        }

    fun searchTeam(searchTerm: String): List<CompanyGroup> =
        realm.query<CompanyGroup>("name CONTAINS[c] $0", searchTerm).find().map {
            CompanyGroup().apply {
                category = it.category
                employees = it.employees
                groupProfileId = it.groupProfileId
                name = it.name
                parentGroupIds = it.parentGroupIds
            }
        }
}
