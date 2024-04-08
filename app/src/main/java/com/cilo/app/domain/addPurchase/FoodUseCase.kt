package com.cilo.app.domain.addPurchase

import com.cilo.app.data.FoodRepository
import com.cilo.app.data.models.Food
import org.mongodb.kbson.ObjectId

class FoodUseCase(private val foodRepository: FoodRepository) {

    fun getFood(): List<Food> = foodRepository.getFood()
    fun getFoodById(id: ObjectId): Food = foodRepository.getFoodById(id)

    fun search(searchTerm: String): List<Food> = foodRepository.searchFood(searchTerm)

    suspend fun saveFood(food: Food) = foodRepository.saveFood(food)

}
