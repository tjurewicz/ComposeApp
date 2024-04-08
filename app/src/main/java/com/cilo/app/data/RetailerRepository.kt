package com.cilo.app.data

import com.cilo.app.data.models.Retailer
import com.cilo.app.data.network.RealmAPI
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import org.mongodb.kbson.ObjectId

class RetailerRepository(private val realm: Realm, private val realmAPI: RealmAPI) {

    fun getRetailers(): List<Retailer> {
        return realm.query<Retailer>().find().map { Retailer().apply {
            _id = it._id
            _partition = it._partition
            name = it.name
            timesPurchasedFrom = it.timesPurchasedFrom
            type = it.type
        } }
    }

    fun searchRetailers(searchTerm: String): RealmResults<Retailer> {
        return realm.query<Retailer>("name CONTAINS[c] $0", searchTerm).find()
    }

    suspend fun saveRetailer(name: String, type: String) {
        val retailer = Retailer().apply {
            _id = ObjectId()
            this.name = name
            timesPurchasedFrom = timesPurchasedFrom.inc()
            this.type = type
        }
        realm.write {
            this.copyToRealm(retailer)
        }
        realmAPI.createRetailer(retailer)
    }

    fun getRetailerByName(retailerName: String): Retailer =
        Retailer().apply {
            val local = realm.query<Retailer>("name == $0", retailerName).first().find() ?: error("Could not find retailer by name")
            _id = local._id
            _partition = local._partition
            name = local.name
            timesPurchasedFrom = local.timesPurchasedFrom
            type = local.type
        }

}