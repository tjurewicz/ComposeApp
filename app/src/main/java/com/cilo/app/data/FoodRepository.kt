package com.cilo.app.data

import com.cilo.app.data.models.Food
import com.cilo.app.data.network.RealmAPI
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import org.mongodb.kbson.ObjectId

class FoodRepository(private val realm: Realm, private val realmAPI: RealmAPI) {

    fun getFood(): List<Food> {
        return realm.query<Food>().find().map {
            Food().apply {
                _id = it._id
                _partition = it._partition
                cilosPerKgsArray = it.cilosPerKgsArray
                defaultCilosPerKg = it.defaultCilosPerKg
                defaultCilosPerKgArray = it.defaultCilosPerKgArray
                kgsPerItemArray = it.kgsPerItemArray
                name = it.name
                origins = it.origins
                seasonDatesArray = it.seasonDatesArray
                sizes = it.sizes
                tier = it.tier
                tierArray = it.tierArray
                types = it.types
            }
        }
    }

    fun searchFood(searchTerm: String): List<Food> {
        return realm.query<Food>("name CONTAINS[c] $0", searchTerm).find().map {
            Food().apply {
                _id = it._id
                _partition = it._partition
                cilosPerKgsArray = it.cilosPerKgsArray
                defaultCilosPerKg = it.defaultCilosPerKg
                defaultCilosPerKgArray = it.defaultCilosPerKgArray
                kgsPerItemArray = it.kgsPerItemArray
                name = it.name
                origins = it.origins
                seasonDatesArray = it.seasonDatesArray
                sizes = it.sizes
                tier = it.tier
                tierArray = it.tierArray
                types = it.types
            }
        }
    }

    fun getFoodById(id: ObjectId): Food {
        return Food().apply {
            val local = realm.query<Food>("_id == $0", id).first().find() ?: Food()
            _id = local._id
            _partition = local._partition
            cilosPerKgsArray = local.cilosPerKgsArray
            defaultCilosPerKg = local.defaultCilosPerKg
            defaultCilosPerKgArray = local.defaultCilosPerKgArray
            kgsPerItemArray = local.kgsPerItemArray
            name = local.name
            origins = local.origins
            seasonDatesArray = local.seasonDatesArray
            sizes = local.sizes
            tier = local.tier
            tierArray = local.tierArray
            types = local.types
        }
    }

    suspend fun saveFood(food: Food) {
        val newFood = Food().apply {
            _id = food._id
            _partition = food._partition
            cilosPerKgsArray = food.cilosPerKgsArray
            defaultCilosPerKg = food.defaultCilosPerKg
            defaultCilosPerKgArray = food.defaultCilosPerKgArray
            kgsPerItemArray = food.kgsPerItemArray
            name = food.name
            origins = food.origins
            seasonDatesArray = food.seasonDatesArray
            sizes = food.sizes
            tier = food.tier
            tierArray = food.tierArray
            types = food.types
        }
        realm.write { copyToRealm(newFood) }
        realmAPI.saveFood(newFood)
    }
}