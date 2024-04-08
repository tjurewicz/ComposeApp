package com.cilo.app.domain.addPurchase

import com.cilo.app.data.RetailerRepository
import com.cilo.app.data.models.Retailer
import io.realm.kotlin.query.RealmResults

class RetailersUseCase(private val retailerRepository: RetailerRepository) {
    fun getRetailers(): List<Retailer> = retailerRepository.getRetailers()
    fun search(searchTerm: String): RealmResults<Retailer> = retailerRepository.searchRetailers(searchTerm)
    suspend fun saveRetailer(name: String, type: String) {
        retailerRepository.saveRetailer(name, type)
    }

    fun getRetailerByName(retailerName: String): Retailer {
        return retailerRepository.getRetailerByName(retailerName)
    }
}
