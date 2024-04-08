package com.cilo.app.domain.addPurchase

import com.cilo.app.data.models.Food
import com.cilo.app.data.models.PurchasedItem
import com.cilo.app.data.models.Target
import com.cilo.app.data.models.Tip
import io.realm.kotlin.types.RealmInstant
import org.mongodb.kbson.ObjectId
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Locale
import java.util.TimeZone

class CalculationUseCase(
    private val foodUseCase: FoodUseCase,
    private val purchasedItemUseCase: PurchasedItemUseCase
) {

    private val tipCategories = arrayOf(
        "differentItem",
        "differentItemSameOrigin",
        "sameItemDifferentType",
        "sameItemDifferentOrigin",
        "itemToTier",
        "outOfSeasonToTier",
        "allOutOfSeasonToTier",
        "differentIngredient",
        "ingredientToTier",
        "cutOut"
    )


    fun calculateCilos(
        food: Food,
        size: Int,
        type: Int,
        origin: Int,
        seasonInt: Int,
        quantity: Double,
        isItemsNotKg: Boolean
    ): Double {
        val defaultCilosPerKg =
            if (food.defaultCilosPerKg.isNullOrBlank()) food.defaultCilosPerKgArray.first() else food.defaultCilosPerKg
        val originOptions = if (food.origins.isNotEmpty()) food.origins.count() else 1
        val seasonOptions =
            if (food.seasonDatesArray.isNotEmpty()) food.seasonDatesArray.count() else 1
        val cilosPerKgs = try {
            getCiloPerKg(food, type, origin, size)
        } catch (e: Exception) {
            defaultCilosPerKg
        }
        return if (isItemsNotKg) {
            // calculate based on number of items
            if (food.kgsPerItemArray.isNotEmpty()) {
                quantity * (cilosPerKgs!!.toDouble() * food.kgsPerItemArray[size].toDouble())
            } else {
                quantity * (cilosPerKgs!!.toDouble())
            }
        } else {
            // calculate based on weight in kg
            (quantity * cilosPerKgs!!.toDouble())
        }
    }

    fun recalculateCilos(purchasedItem: PurchasedItem, seasonInt: Int) {
//        purchasedItem.origin
//        val seasonOptions = if (purchasedItem.seasonDatesArray.isNotEmpty()) purchasedItem.seasonDatesArray.count() else 1
//        val index = purchasedItem.type * purchasedItem.origin * seasonOptions + seasonOptions * purchasedItem.origin + seasonInt
//        val cilosPerKgs = try {
//            food.cilosPerKgsArray[index]
//        } catch (e: Exception) {
//            defaultCilosPerKg
//        }
//        return if (isItemsNotKg) {
//            // calculate based on number of items
//            quantity * (cilosPerKgs!!.toDouble() * food.kgsPerItemArray[sizeIndex].toDouble())
//        } else {
//            // calculate based on weight in kg
//            (quantity * cilosPerKgs!!.toDouble())
//        }
    }

    private fun getSeasonArrayPosition(seasonDates: List<Int>, purchaseMonth: Int): Int {
        for (i in seasonDates.indices step 2) {
            if (isPurchaseMonthInSeasonRange(seasonDates[i], seasonDates[i + 1], purchaseMonth)) {
                return i / 2
            }
        }
        return seasonDates.size / 2
    }

    private fun isPurchaseMonthInSeasonRange(
        seasonStartMonth: Int,
        seasonEndMonth: Int,
        purchaseMonth: Int
    ): Boolean {
        return if (seasonStartMonth < seasonEndMonth) {
            purchaseMonth in seasonStartMonth..seasonEndMonth
        } else {
            purchaseMonth >= seasonStartMonth || purchaseMonth <= seasonEndMonth
        }
    }

    internal fun getItemSeasonText(item: Food, purchaseMonth: Int): String? {
        val defaultCilosPerKgArray = item.defaultCilosPerKgArray.mapNotNull { it.toDoubleOrNull() }
        if (defaultCilosPerKgArray.isNotEmpty()) {
            val (inSeasonDateInt, outSeasonDateInt) = defaultCilosPerKgArray.withIndex()
                .fold(Pair(0, 0)) { (inIndex, outIndex), (index, ciloPerKg) ->
                    if (ciloPerKg < defaultCilosPerKgArray[inIndex]) Pair(index, outIndex)
                    else if (ciloPerKg > defaultCilosPerKgArray[outIndex]) Pair(inIndex, index)
                    else Pair(inIndex, outIndex)
                }

            val numberOfSeasons = defaultCilosPerKgArray.size
            val earlySeasonDateInt = (inSeasonDateInt - 1 + numberOfSeasons) % numberOfSeasons
            val lateSeasonDateInt = (inSeasonDateInt + 1) % numberOfSeasons

            return when (getSeasonArrayPosition(item.seasonDatesArray, purchaseMonth)) {
                inSeasonDateInt -> "In Season"
                outSeasonDateInt -> "Out of season"
                earlySeasonDateInt -> "Early season"
                lateSeasonDateInt -> "Late season"
                else -> {
                    println("ERROR checking which season item is currently in, unexpected case returned. Purchase month = $purchaseMonth, inSeasonDateInt = $inSeasonDateInt, outSeasonDateInt = $outSeasonDateInt, earlySeasonDateInt = $earlySeasonDateInt, lateSeasonDateInt = $lateSeasonDateInt")
                    null
                }
            }
        } else {
            println("Error checking if item in season, first item in defaultCilosPerKg array could not be converted to double.")
            return null
        }
    }

    fun getCiloPerKg(
        selectedItem: Food?,
        selectedType: Int,
        selectedOrigin: Int,
        seasonInt: Int
    ): String {
        if (selectedItem != null) {
            if (selectedItem.cilosPerKgsArray.isNotEmpty()) {
                var numberOfOrigins = 1
                if (selectedItem.origins.isNotEmpty()) {
                    numberOfOrigins = selectedItem.origins.size
                }
                var numberOfSeasonOptions = 1
                if (selectedItem.seasonDatesArray.size == 2) {
                    numberOfSeasonOptions = 2
                } else if (selectedItem.seasonDatesArray.size == 3 || selectedItem.seasonDatesArray.size == 4) {
                    numberOfSeasonOptions = 3
                }
                val ciloToSelect =
                    selectedType * numberOfOrigins * numberOfSeasonOptions +
                            numberOfSeasonOptions * selectedOrigin +
                            seasonInt
                return try {
                    selectedItem.cilosPerKgsArray[ciloToSelect]
                }
                catch (e: Exception) {
                    selectedItem.defaultCilosPerKg ?: ""
                }
            } else if (selectedItem.defaultCilosPerKg != null) {
                return selectedItem.defaultCilosPerKg!!
            } else {
                println("ERROR getting CiloPerKg value, default CilosPerKg is null.")
                return "0.00"
            }
        } else {
            println("ERROR getting hold of CiloPerKg value, selected item is null.")
            return "0.00"
        }
    }

    private fun groupPurchasedItemsByTypeAndOrigin(
        purchasedItems: List<PurchasedItem>,
        typesCount: Int
    ): List<Map<Int, List<PurchasedItem>>> {
        val filteredPurchasedItemsGroupsArray = mutableListOf<Map<Int, List<PurchasedItem>>>()

        val filteredPurchasedItemsGroupedByType = purchasedItems.groupBy { it.typeNumber }

        for (type in 0 until typesCount) {
            val filteredPurchasedItemsGroupedByOrigin =
                filteredPurchasedItemsGroupedByType[type]?.groupBy { it.originNumber } ?: emptyMap()

            filteredPurchasedItemsGroupsArray.add(filteredPurchasedItemsGroupedByOrigin)
        }

        return filteredPurchasedItemsGroupsArray
    }

    private fun filterItemsByTier(
        purchasedItems: List<PurchasedItem>,
        tier: Long
    ): List<PurchasedItem> {
        var purchasedItemsFilteredByTier = purchasedItems.toMutableList()
        println("purchased items count - ${purchasedItemsFilteredByTier.size}")

        if (tier >= 1) {
            purchasedItemsFilteredByTier =
                purchasedItemsFilteredByTier.filter { it.tier != "one" }.toMutableList()
        }
        if (tier >= 2) {
            purchasedItemsFilteredByTier =
                purchasedItemsFilteredByTier.filter { it.tier != "two" }.toMutableList()
        }
        if (tier >= 3) {
            purchasedItemsFilteredByTier =
                purchasedItemsFilteredByTier.filter { it.tier != "three" }.toMutableList()
        }

        return purchasedItemsFilteredByTier
    }

    private fun checkIfPurchasedItemOutOfSeason(
        startOfSeasonInt: Int,
        endOfSeasonInt: Int,
        monthInt: Int
    ): Boolean {
        return ((startOfSeasonInt < endOfSeasonInt && (monthInt < startOfSeasonInt || monthInt > endOfSeasonInt))
                || (monthInt in (endOfSeasonInt + 1)..<startOfSeasonInt))
    }


    private fun calculateTotalCilos(
        filteredPurchasedItems: List<PurchasedItem>,
        parentItem: Food,
        tip: Tip,
        parentItemReferenceInt: Int
    ): Double {
        if (filteredPurchasedItems.isNotEmpty()) {
            // get the associated food item

            when (tip.category) {
                tipCategories[0] -> {
                    // if tip category is differentItem
                    val kgs = filteredPurchasedItems.sumOf { it.kgs }
                    return calculateCilosForTarget(filteredPurchasedItems, parentItem, kgs)
                }

                tipCategories[7] -> {
                    // if tip category is "differentItemSameOrigin"
                    val kgs = filteredPurchasedItems.sumOf { it.kgs }
                    val parentItemPercentage = parentItemPercentage(parentItemReferenceInt, tip)
                    return calculateCilosForTarget(
                        filteredPurchasedItems,
                        parentItem,
                        kgs,
                        parentItemPercentage
                    )
                }

                tipCategories[1] -> {
                    // if tip category is "sameItemDifferentType"
                    val filteredPurchasedItemsGrouped =
                        filteredPurchasedItems.groupBy { it.originNumber }
                    for (origin in 0 until parentItem.origins.size) {
                        val kgs = filteredPurchasedItemsGrouped[origin]?.sumOf { it.kgs }
                        val parentCiloPerKg = parentItem.cilosPerKgsArray[origin].toDoubleOrNull()
                        if (kgs != null && parentCiloPerKg != null) {
                            return parentCiloPerKg * kgs
                        }
                    }
                }

                tipCategories[2], tipCategories[3] -> {
                    // if tip category is "sameItemDifferentOrigin"
                    val typesCount = getTypesCount(parentItem)
                    val originsCount = getOriginsCount(parentItem)
                    val filteredPurchasedItemsGroupsArray =
                        groupPurchasedItemsByTypeAndOrigin(filteredPurchasedItems, typesCount)
                    for (type in 0 until typesCount) {
                        for (origin in 0 until originsCount) {
                            val group = filteredPurchasedItemsGroupsArray[type][origin]
                            val ciloPerKg =
                                parentItem.cilosPerKgsArray[type * originsCount + origin].toDoubleOrNull()
                            if (group != null && ciloPerKg != null) {
                                val kgs = group.sumOf { it.kgs }
                                return kgs * ciloPerKg
                            }
                        }
                    }
                }

                tipCategories[4], tipCategories[8] -> {
                    // if tip category is itemToTier
                    val parentItemPercentage = parentItemPercentage(parentItemReferenceInt, tip)
                    val kgs = filteredPurchasedItems.sumOf { it.kgs } * parentItemPercentage
                    val ciloPerKg = parentItem.defaultCilosPerKg?.toDoubleOrNull() ?: 0.0
                    return ciloPerKg * kgs
                }

                tipCategories[5], tipCategories[6] -> {
                    // if tip category is out of season to tier tip
                    val purchasedItemsFilteredByTier =
                        filterItemsByTier(filteredPurchasedItems, tip.alternativeTier ?: 0)
                    val seasonInts = getSeasonsInts(parentItem)
                    val startOfSeasonInt = seasonInts[0]
                    val endOfSeasonInt = seasonInts[1]
                    val seasonsCount = parentItem.seasonDatesArray.size - 1
                    val originsCount = getOriginsCount(parentItem)
                    val typesCount = getTypesCount(parentItem)
                    var totalCilos = 0.0
                    val parentItemPercentage = parentItemPercentage(parentItemReferenceInt, tip)
                    for (purchasedItem in purchasedItemsFilteredByTier) {
                        val date = purchasedItem.date
                        if (date != null) {
                            val instant = Instant.ofEpochMilli(date.epochSeconds)
                            val zonedDateTime =
                                ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
                            val monthInt = zonedDateTime.monthValue
                            if (checkIfPurchasedItemOutOfSeason(
                                    startOfSeasonInt,
                                    endOfSeasonInt,
                                    monthInt
                                )
                            ) {
                                val seasonInt =
                                    getSeasonArrayPosition(
                                        parentItem.seasonDatesArray,
                                        monthInt
                                    )
                                if (originsCount > 1 || typesCount > 1) {
                                    val cilosPerKgArrayPosition =
                                        purchasedItem.typeNumber * seasonsCount * originsCount + purchasedItem.originNumber * seasonsCount + seasonInt
                                    val cilosPerKgArrayCount = parentItem.cilosPerKgsArray.size
                                    if (cilosPerKgArrayPosition < cilosPerKgArrayCount) {
                                        val cilosPerKg =
                                            parentItem.cilosPerKgsArray[cilosPerKgArrayPosition].toDoubleOrNull()
                                        if (cilosPerKg != null) {
                                            totalCilos +=
                                                cilosPerKg * purchasedItem.kgs * parentItemPercentage
                                        }
                                    } else {
                                        println("ERROR cilos per kg array position is larger than cilos per kg array count for purchased item - ${purchasedItem._id}")
                                    }
                                } else {
                                    val cilosPerKg =
                                        parentItem.defaultCilosPerKgArray[seasonInt].toDoubleOrNull()
                                    if (cilosPerKg != null) {
                                        totalCilos +=
                                            cilosPerKg * purchasedItem.kgs * parentItemPercentage
                                    }
                                }
                            }
                        } else {
                            println("ERROR calculating potential saving for a particular food item within a tip for category 5, purchased item has no date")
                        }
                    }
                    return totalCilos
                }

                tipCategories[tipCategories.size - 1] -> {
                    return calculateCilosForTarget(
                        filteredPurchasedItems,
                        parentItem,
                        parentItemPercentage(parentItemReferenceInt, tip)
                    )
                }

                else -> {
                    println("ERROR calculating potential saving for a particular food item within a tip, unexpected tip category found - ${tip.category}")
                }
            }
        }
        // else no purchased items found
        return 0.0
    }

    private fun getTypeWithLowestCilosPerKg(
        parentItem: Food,
        typesCount: Int,
        originsCount: Int
    ): Int {
        var typeWithLowestCiloPerKg = 0
        var lowestCiloPerKg = 0.0

        for (type in 0 until typesCount) {
            val cilosPerKg = parentItem.cilosPerKgsArray[type * originsCount].toDoubleOrNull()

            if (type == 0 || (cilosPerKg != null && cilosPerKg < lowestCiloPerKg)) {
                lowestCiloPerKg = cilosPerKg ?: 0.0
                typeWithLowestCiloPerKg = type
            }
        }

        return typeWithLowestCiloPerKg
    }

    private fun getOriginWithLowestCilosPerKg(parentItem: Food, originsCount: Int): Int {
        var originWithLowestCiloPerKg = 0
        var lowestCiloPerKg = 0.0

        for (origin in 0 until originsCount) {
            val cilosPerKg = parentItem.cilosPerKgsArray[origin].toDoubleOrNull()

            if (origin == 0 || (cilosPerKg != null && cilosPerKg < lowestCiloPerKg)) {
                lowestCiloPerKg = cilosPerKg ?: 0.0
                originWithLowestCiloPerKg = origin
            }
        }

        return originWithLowestCiloPerKg
    }

    internal fun calculatePotentialSaving(
        highCarbonItemsPurchased: List<PurchasedItem>,
        tip: Tip,
        parentItemReferenceInt: Int,
        highCarbonIngredientParentItem: Food?,
        potentialSavingPerKgForIngredientToTier: Double?
    ): Double {
        if (highCarbonItemsPurchased.isNotEmpty()) {
            // get the associated food item
            val parentItem = foodUseCase.getFoodById(tip.parentItemReferences[0])
            println("")
            println(parentItem.name)

            if (tip.category == tipCategories[0]
                || tip.category == tipCategories[1]
                || tip.category == tipCategories[7]
            ) {

                val lowCarbonAlternative = foodUseCase.getFoodById(tip.lowCarbonAlternativeReferences[0])

                when (tip.category) {
                    tipCategories[0] -> {
                        // if tip category is differentItem
                        val kgs = highCarbonItemsPurchased.sumOf { it.kgs }
                        val cilos = calculateCilosForTarget(highCarbonItemsPurchased, parentItem, kgs)
                        val alternativeCilos =
                            calculateAlternativeCilos(parentItem, lowCarbonAlternative, kgs)

                        val potentialSaving = cilos - alternativeCilos
                        if (potentialSaving > 0) {
                            return potentialSaving
                        }
                    }

                    tipCategories[1] -> {
                        // if tip category is "differentItemSameOrigin"
                        val filteredPurchasedItemsGrouped =
                            highCarbonItemsPurchased.groupBy { it.originNumber }

                        for (origin in 0 until parentItem.origins.size) {
                            val kgs =
                                filteredPurchasedItemsGrouped[origin]?.sumOf { it.kgs }
                                    ?: continue
                            val parentCiloPerKg =
                                parentItem.cilosPerKgsArray[origin].toDoubleOrNull()
                                    ?: continue
                            val alternativeCiloPerKg =
                                lowCarbonAlternative.cilosPerKgsArray[origin].toDoubleOrNull()
                                    ?: continue

                            return (parentCiloPerKg - alternativeCiloPerKg) * kgs
                        }
                    }

                    tipCategories[7] -> {
                        // if tip category is differentIngredient
                        val parentItemPercentage =
                            parentItemPercentage(parentItemReferenceInt, tip)
                        val kgs = highCarbonItemsPurchased.sumOf { it.kgs }

                        if (highCarbonIngredientParentItem != null) {
                            val cilos: Double = if (parentItemReferenceInt == 0) {
                                calculateCilosForTarget(
                                    highCarbonItemsPurchased,
                                    highCarbonIngredientParentItem,
                                    kgs,
                                    parentItemPercentage
                                )
                            } else {
                                (highCarbonIngredientParentItem.defaultCilosPerKg ?:
                                highCarbonIngredientParentItem.cilosPerKgsArray.first()).toDouble() * kgs * parentItemPercentage
                            }

                            val alternativeCilos =
                                calculateAlternativeCilos(
                                    parentItem,
                                    lowCarbonAlternative,
                                    kgs,
                                    parentItemPercentage
                                )

                            val potentialSaving = cilos - alternativeCilos
                            if (potentialSaving > 0) {
                                return potentialSaving
                            } else {
                                println("PROBABLE ERROR - potential saving less than 0")
                            }
                        }
                    }
                }
            }

            // if tip category is "sameItemDifferentType" or "sameItemDifferentOrigin"
            else if (tip.category == tipCategories[2]
                || tip.category == tipCategories[3]
            ) {
                val typesCount = getTypesCount(parentItem)
                val originsCount = getOriginsCount(parentItem)
                var groupWithLowestCiloPerKg = 0

                val filteredPurchasedItemsGroupsArray =
                    groupPurchasedItemsByTypeAndOrigin(highCarbonItemsPurchased, typesCount)

                if (tip.category == tipCategories[2]) {
                    groupWithLowestCiloPerKg =
                        getTypeWithLowestCilosPerKg(parentItem, typesCount, originsCount)
                } else if (tip.category == tipCategories[3]) {
                    groupWithLowestCiloPerKg =
                        getOriginWithLowestCilosPerKg(parentItem, originsCount)
                }

                for (type in 0 until typesCount) {
                    var bestCilosPerKg: Double? = null
                    if (tip.category == tipCategories[3]) {
                        bestCilosPerKg =
                            parentItem.cilosPerKgsArray[groupWithLowestCiloPerKg + (originsCount * type)]
                                .toDoubleOrNull()
                    } else if (type == groupWithLowestCiloPerKg) {
                        continue
                    }

                    for (origin in 0 until originsCount) {
                        if (tip.category == tipCategories[2]) {
                            bestCilosPerKg =
                                parentItem.cilosPerKgsArray[origin + (originsCount * groupWithLowestCiloPerKg)]
                                    .toDoubleOrNull()
                        } else if (origin == groupWithLowestCiloPerKg) {
                            continue
                        }

                        val group = filteredPurchasedItemsGroupsArray[type][origin]
                        val ciloPerKg = parentItem.cilosPerKgsArray[(type * originsCount) + origin]
                            .toDoubleOrNull()

                        if (group != null && bestCilosPerKg != null && ciloPerKg != null) {
                            val kgs = group.sumOf { it.kgs }
                            val potentialSavingPerKg = ciloPerKg - bestCilosPerKg
                            if (potentialSavingPerKg > 0) {
                                return kgs * potentialSavingPerKg
                            }
                        }
                    }
                }
            }

            // if tip category is itemToTier or ingredientToTier
            else if (tip.category == tipCategories[4]
                || tip.category == tipCategories[8]
            ) {
                var kgs = highCarbonItemsPurchased.sumOf { it.kgs }
                if (tip.category == tipCategories[8]) {
                    val parentItemPercentage =
                        parentItemPercentage(parentItemReferenceInt, tip)
                    kgs *= parentItemPercentage
                }

                var potentialSavingPerKg: Double?
                if (tip.category == tipCategories[4]) {
                    potentialSavingPerKg =
                        (parentItem.defaultCilosPerKg?.toDoubleOrNull()
                            ?: 0.0) - getReplacementCilosPerKg(
                            tip.alternativeTier
                        )
                } else {
                    potentialSavingPerKg = potentialSavingPerKgForIngredientToTier
                }

                if (potentialSavingPerKg != null) {
                    val potentialSaving = potentialSavingPerKg * kgs
                    if (potentialSaving > 0) {
                        return potentialSaving
                    }
                }
            }

            // if tip category is out of season to tier tip
            else if (tip.category == tipCategories[5]
                || tip.category == tipCategories[6]
            ) {
                val purchasedItemsFilteredByTier =
                    filterItemsByTier(highCarbonItemsPurchased, tip.alternativeTier ?: 0)

                val seasonInts = getSeasonsInts(parentItem)
                val startOfSeasonInt = seasonInts[0]
                val endOfSeasonInt = seasonInts[1]
                val currentMonthInt =
                    ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).monthValue

                val seasonsCount = parentItem.seasonDatesArray.size - 1
                val originsCount = getOriginsCount(parentItem)
                val typesCount = getTypesCount(parentItem)

                var totalKgs = 0.0
                var totalCilos = 0.0

                val parentItemPercentage =
                    parentItemPercentage(parentItemReferenceInt, tip)

                for (purchasedItem in purchasedItemsFilteredByTier) {
                    val date = purchasedItem.date
                        ?: error("ERROR calculating potential saving for a particular food item within a tip for category 5, purchased item has no date")
                    val instant = Instant.ofEpochMilli(date.epochSeconds)
                    val zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
                    val monthInt = zonedDateTime.monthValue
                    if (checkIfPurchasedItemOutOfSeason(
                            startOfSeasonInt,
                            endOfSeasonInt,
                            monthInt
                        )
                    ) {
                        val seasonInt =
                            getSeasonArrayPosition(
                                parentItem.seasonDatesArray,
                                purchaseMonth = monthInt
                            )
                        if (originsCount > 1 || typesCount > 1) {
                            val cilosPerKgArrayPosition =
                                (purchasedItem.typeNumber * seasonsCount * originsCount) +
                                        (purchasedItem.originNumber * seasonsCount) +
                                        seasonInt
                            val cilosPerKgArrayCount = parentItem.cilosPerKgsArray.size

                            if (cilosPerKgArrayPosition < cilosPerKgArrayCount) {
                                val cilosPerKg =
                                    parentItem.cilosPerKgsArray[cilosPerKgArrayPosition].toDoubleOrNull()
                                        ?: continue
                                totalKgs += purchasedItem.kgs * parentItemPercentage
                                totalCilos += cilosPerKg * purchasedItem.kgs
                            } else {
                                println("ERROR cilos per kg array position is larger than cilos per kg array count for purchased item - ${purchasedItem._id}")
                            }
                        } else {
                            val cilosPerKg =
                                parentItem.defaultCilosPerKgArray[seasonInt].toDoubleOrNull()
                                    ?: continue
                            totalKgs += purchasedItem.kgs
                            totalCilos += cilosPerKg * purchasedItem.kgs
                        }
                    }
                }
                if (totalKgs > 0 && totalCilos > 0) {
                    val alternativeCilos =
                        totalKgs * getReplacementCilosPerKg(tip.alternativeTier)
                    val potentialSaving = totalCilos - alternativeCilos
                    if (potentialSaving > 0) {
                        return potentialSaving
                    } else {
                        println("ERROR calculating potential saving for a particular food item within a tip for category 6, potential saving unexpectedly found not to be greater than 0")
                    }
                }
            }

            // if tip category is the last category
            else if (tip.category == tipCategories[tipCategories.size - 1]) {
                return calculateCilosForTarget(
                    highCarbonItemsPurchased,
                    parentItem,
                    parentItemPercentage(parentItemReferenceInt, tip)
                )
            } else {
                println("ERROR calculating potential saving for a particular food item within a tip, unexpected tip category found - ${tip.category}")
            }
        } else {
            println("ERROR calculating potential saving for a particular food item within a tip, no associated food item found")
        }
        // else no purchased items found
        return 0.0
    }

    private fun getReplacementCilosPerKg(tier: Long?): Double {
        return when (tier) {
            1L -> 0.75
            2L -> 1.5
            3L -> 3.0
            4L -> 6.0
            5L -> 12.0
            else -> 1.5
        }
    }

    internal fun getPotentialSaving(target: Target, months: Int): Double {
        return target.firstMonthSaving?.times(months) ?: 0.0
    }

    fun getSavingsOrPointsStrings(
        ciloSavings: Double,
        purchases: List<PurchasedItem>
    ): String {
        val sdf = SimpleDateFormat("d MMM, uuuu", Locale.UK)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val groupingDateFormat = SimpleDateFormat("yyyy MM", Locale.UK)
        groupingDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val groupedPurchases =
            purchases.groupBy { groupingDateFormat.format(it.date!!.epochSeconds * 1000L) }
        val listOfMonthlyCosts = groupedPurchases.values.map { weekPurchases ->
            weekPurchases.sumOf { it.ciloCost }
        }
        val averageSpend = listOfMonthlyCosts.sumOf { it } / listOfMonthlyCosts.size
        val newPotentialSaving = ciloSavings / listOfMonthlyCosts.size
        return String.format("%.2f", (newPotentialSaving / averageSpend) * 100)
    }


    fun calculateTotalPotentialSaving(tip: Tip, purchasedItems: List<PurchasedItem>): Double {
        var totalPotentialSaving = 0.0

        // The below means that the function calculatePotentialSaving(...) doesn't have to keep calculating the same thing again and again
        var highCarbonIngredientParentItem: Food? = null
        var potentialSavingPerKgForIngredientToTier: Double? = null

        if (tip.category == tipCategories[7] || tip.category == tipCategories[8]) {
            highCarbonIngredientParentItem = foodUseCase.getFoodById(tip.parentItemReferences[0])
            if (tip.category == tipCategories[8]) {
                potentialSavingPerKgForIngredientToTier =
                    (highCarbonIngredientParentItem.defaultCilosPerKg ?: highCarbonIngredientParentItem.cilosPerKgsArray.first()).toDouble() - getReplacementCilosPerKg(tip.alternativeTier)
            }
        }

        for (parentItemReferenceInt in 0 until tip.parentItemReferences.size) {
            val parentItemFood = filterPurchasedItems(
                purchasedItems,
                tip.parentItemReferences[parentItemReferenceInt]
            )
            totalPotentialSaving += calculatePotentialSaving(
                parentItemFood,
                tip,
                parentItemReferenceInt,
                highCarbonIngredientParentItem,
                potentialSavingPerKgForIngredientToTier
            )
        }
        return String.format("%.2f", totalPotentialSaving).toDouble()
    }

    private fun filterPurchasedItems(
        purchasedItems: List<PurchasedItem>,
        reference: ObjectId
    ): List<PurchasedItem> {
        return purchasedItems.filter { it.correspondingItem_id == reference }
    }


    private fun getTypesCount(parentItem: Food): Int {
        var typesCount = parentItem.types.size
        if (typesCount == 0) {
            typesCount = 1
        }
        return typesCount
    }

    private fun getOriginsCount(parentItem: Food): Int {
        var originsCount = parentItem.origins.size
        if (originsCount == 0) {
            originsCount = 1
        }
        return originsCount
    }

    private fun getSeasonsCount(parentItem: Food): Int {
        var seasonsCount = parentItem.defaultCilosPerKgArray.size
        if (seasonsCount == 0) {
            seasonsCount = 1
        }
        return seasonsCount
    }

    private fun calculateCilosForTarget(
        purchasedItems: List<PurchasedItem>,
        parentItem: Food,
        kgs: Double? = null,
        parentItemPercentage: Double = 1.0
    ): Double {
        var cilos: Double = 0.0

        if (parentItem.cilosPerKgsArray.size < 2) {
            if (parentItem.defaultCilosPerKg != null && parentItem.defaultCilosPerKg!!.isNotEmpty()) {
                val itemCilosPerKg = parentItem.defaultCilosPerKg!!.toDoubleOrNull()
                if (itemCilosPerKg != null) {
                    cilos = itemCilosPerKg * (kgs
                        ?: purchasedItems.sumOf { it.kgs }) * parentItemPercentage
                } else {
                    if (parentItem.defaultCilosPerKgArray.isNotEmpty()) {
                        for (purchasedItem in purchasedItems) {
                            val date = purchasedItem.date
                            if (date != null) {
                                val instant = Instant.ofEpochMilli(date.epochSeconds)
                                val zonedDateTime =
                                    ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
                                val monthInt = zonedDateTime.monthValue
                                val seasonInt =
                                    getSeasonArrayPosition(parentItem.seasonDatesArray, monthInt)
                                val cilosPerKg =
                                    parentItem.defaultCilosPerKgArray[seasonInt].toDoubleOrNull()
                                if (cilosPerKg != null) {
                                    cilos += cilosPerKg * purchasedItem.kgs * parentItemPercentage
                                }
                            }
                        }
                    }
                }
            }
        } else {
            val typesCount = getTypesCount(parentItem)
            val originsCount = getOriginsCount(parentItem)

            if (parentItem.seasonDatesArray.size < 2) {
                val groupedItems = groupPurchasedItemsByTypeAndOrigin(purchasedItems, typesCount)
                for (type in 0 until typesCount) {
                    for (origin in 0 until originsCount) {
                        val group = groupedItems[type][origin]
                        val ciloPerKg =
                            parentItem.cilosPerKgsArray[type * originsCount + origin].toDoubleOrNull()

                        if (group != null && ciloPerKg != null) {
                            val totalKgs = group.sumOf { it.kgs } * parentItemPercentage
                            cilos += ciloPerKg * totalKgs
                        }
                    }
                }
            } else {
                val seasonInts = getSeasonsInts(parentItem)
                val startOfSeasonInt = seasonInts[0]
                val endOfSeasonInt = seasonInts[1]
                val seasonsCount = parentItem.seasonDatesArray.size - 1

                for (purchasedItem in purchasedItems) {
                    val date = purchasedItem.date
                    if (date != null) {
                        val instant = Instant.ofEpochMilli(date.epochSeconds)
                        val zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
                        val monthInt = zonedDateTime.monthValue
                        val seasonInt =
                            getSeasonArrayPosition(parentItem.seasonDatesArray, monthInt)
                        val cilosPerKgArrayPosition =
                            purchasedItem.typeNumber * seasonsCount * originsCount + purchasedItem.originNumber * seasonsCount + seasonInt
                        val cilosPerKgArrayCount = parentItem.cilosPerKgsArray.size

                        if (cilosPerKgArrayPosition < cilosPerKgArrayCount) {
                            val cilosPerKg =
                                parentItem.cilosPerKgsArray[cilosPerKgArrayPosition].toDoubleOrNull()
                            if (cilosPerKg != null) {
                                cilos += cilosPerKg * purchasedItem.kgs * parentItemPercentage
                            }
                        } else {
                            println("ERROR cilos per kg array position is larger than cilos per kg array count for purchased item - ${purchasedItem._id}")
                        }
                    }
                }
            }
        }

        if (cilos == 0.0) {
            cilos = purchasedItems.sumOf { it.ciloCost }
        }

        return cilos
    }

    private fun getSeasonsInts(parentItem: Food): IntArray {
        // get month int when item is in season
        var startOfSeasonInt = 0
        var endOfSeasonInt = 0
        var lowestCilosPerKg: Double? = null
        val seasonsCount = parentItem.seasonDatesArray.size - 1

        for (seasonInt in 0..seasonsCount) {
            val cilosPerKg =
                parentItem.defaultCilosPerKgArray.getOrNull(seasonInt)?.toDouble() ?: continue

            if (seasonInt == 0) {
                startOfSeasonInt = parentItem.seasonDatesArray[seasonInt]
                endOfSeasonInt = parentItem.seasonDatesArray[seasonInt + 1]
                lowestCilosPerKg = cilosPerKg
            } else {
                if (cilosPerKg < (lowestCilosPerKg ?: 0.0)) {
                    startOfSeasonInt = parentItem.seasonDatesArray[seasonInt] + 1
                    lowestCilosPerKg = cilosPerKg

                    if (seasonInt < seasonsCount) {
                        endOfSeasonInt = parentItem.seasonDatesArray[seasonInt + 1]
                    } else {
                        endOfSeasonInt = if (parentItem.seasonDatesArray[0] == 1) {
                            12
                        } else {
                            parentItem.seasonDatesArray[0] - 1
                        }
                    }
                }
            }
        }

        return intArrayOf(startOfSeasonInt, endOfSeasonInt)
    }


    private fun calculateAlternativeCilos(
        parentItem: Food,
        lowCarbonAlternative: Food,
        kgs: Double,
        parentItemPercentage: Double = 1.0
    ): Double {
        var alternativeCilos: Double = 0.0

        if (lowCarbonAlternative.cilosPerKgsArray.size < 2) {
            val alternativeCilosPerKg = lowCarbonAlternative.defaultCilosPerKg?.toDoubleOrNull()
            if (alternativeCilosPerKg != null) {
                alternativeCilos = alternativeCilosPerKg * kgs * parentItemPercentage
            } else {
                if (lowCarbonAlternative.defaultCilosPerKgArray.isNotEmpty()) {
                    val alternativeCilosPerKg =
                        parentItem.defaultCilosPerKgArray[0].toDoubleOrNull()
                    if (alternativeCilosPerKg != null) {
                        alternativeCilos = alternativeCilosPerKg * kgs * parentItemPercentage
                    }
                }
            }
        }

        return alternativeCilos
    }


    private fun parentItemPercentage(int: Int, tip: Tip): Double {
        return if (int < tip.parentItemPercentages.size) {
            tip.parentItemPercentages[int]
        } else {
            1.0
        }
    }

    fun getLostSavings(realmTip: Tip, parentItemReferences: List<PurchasedItem>): Double {
        var lostSavings = 0.0

        // The below means that the function calculatePotentialSaving(...) doesn't have to keep calculating the same thing again and again
        var highCarbonIngredientParentItem: Food? = Food()
        var potentialSavingPerKgForIngredientToTier: Double? = 0.0

        if (realmTip.category == tipCategories[7] || realmTip.category == tipCategories[8]) {
            highCarbonIngredientParentItem = foodUseCase.getFoodById(realmTip.parentItemReferences[0])
            if (realmTip.category == tipCategories[8]) {
                potentialSavingPerKgForIngredientToTier =
                    (highCarbonIngredientParentItem.defaultCilosPerKg?.toDoubleOrNull() ?: 0.0) -
                            getReplacementCilosPerKg(realmTip.alternativeTier)
            }
        }
        realmTip.parentItemReferences.forEachIndexed { index, _ ->
            lostSavings += calculatePotentialSaving(parentItemReferences, realmTip, index, highCarbonIngredientParentItem, potentialSavingPerKgForIngredientToTier)
        }
        return lostSavings
    }

    internal fun getMinimumPotentialSavingRequiredForPoints(targets: List<Target>): Double {
        if (true) {
            val now = RealmInstant.now().epochSeconds * 1000L
            val currentTargets = targets.filter { it.finishDate!!.epochSeconds * 1000L >= now }
            if (currentTargets.size > 2) {
                return currentTargets.map { it.potentialSaving ?: 0.0 }.sortedDescending()[2]
            }
        }
        return 0.0
    }
}