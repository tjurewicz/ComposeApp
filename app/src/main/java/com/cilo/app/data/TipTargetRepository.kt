package com.cilo.app.data

import com.cilo.app.data.models.Target
import com.cilo.app.data.models.Tip
import com.cilo.app.data.network.RealmAPI
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

class TipTargetRepository(private val realm: Realm, private val realmAPI: RealmAPI) {

    fun getTips(): List<Tip> {
        return realm.query<Tip>().find().map {
            Tip().apply {
                _id = it._id
                _partition = it._partition
                alternativeTier = it.alternativeTier
                category = it.category
                explanation = it.explanation
                highCarbonItemsCollectiveName = it.highCarbonItemsCollectiveName
                lowCarbonAlternativeReferences = it.lowCarbonAlternativeReferences
                lowCarbonItemsCollectiveName = it.lowCarbonItemsCollectiveName
                name = it.name
                parentItemPercentages = it.parentItemPercentages
                parentItemReferences = it.parentItemReferences
            }
        }
    }

    fun getTargets(): List<Target> = realm.query<Target>().find().map {
        Target().apply {
            _id = it._id
            _partition = it._partition
            associatedTip_id = it.associatedTip_id
            beginDate = it.beginDate
            finishDate = it.finishDate
            potentialSaving = it.potentialSaving
            firstMonthSaving = it.firstMonthSaving
            reductionFactor = it.reductionFactor
        }
    }

    suspend fun setTarget(target: Target) {
        realm.write {
            this.copyToRealm(target)
        }
        realmAPI.createTarget(target)
    }

    suspend fun deleteTarget(target: Target) {
        realm.write { delete(this.query<Target>("_id == $0", target._id).find()) }
        realmAPI.deleteTarget(target)
    }

    fun getTipById(id: BsonObjectId): Tip = Tip().apply {
        val local = realm.query<Tip>("_id == $0", id).find().first()
        _id = local._id
        _partition = local._partition
        alternativeTier = local.alternativeTier
        category = local.category
        explanation = local.explanation
        highCarbonItemsCollectiveName = local.highCarbonItemsCollectiveName
        lowCarbonAlternativeReferences = local.lowCarbonAlternativeReferences
        lowCarbonItemsCollectiveName = local.lowCarbonItemsCollectiveName
        name = local.name
        parentItemPercentages = local.parentItemPercentages
        parentItemReferences = local.parentItemReferences
    }

    fun getTargetById(id: BsonObjectId): Target = Target().apply {
        val local = realm.query<Target>("_id == $0", id).find().first()
        _id = local._id
        _partition = local._partition
        associatedTip_id = local.associatedTip_id
        beginDate = local.beginDate
        finishDate = local.finishDate
        potentialSaving = local.potentialSaving
        firstMonthSaving = local.firstMonthSaving
        reductionFactor = local.reductionFactor
    }

    fun getTargetByAssociatedTipId(tipId: ObjectId) : List<Target> =
        realm.query<Target>("associatedTip_id == $0", tipId).find().map {
            Target().apply {
                _id = it._id
                _partition = it._partition
                associatedTip_id = it.associatedTip_id
                beginDate = it.beginDate
                finishDate = it.finishDate
                potentialSaving = it.potentialSaving
                firstMonthSaving = it.firstMonthSaving
                reductionFactor = it.reductionFactor
            }
        }

    suspend fun updateTarget(newTarget: Target) {
        realm.write {
            val target = this.query<Target>("_id == $0", newTarget._id).first().find() ?: error("Could not find target when updating")
            target.beginDate = newTarget.beginDate
            target.finishDate = newTarget.finishDate
            target.potentialSaving = newTarget.potentialSaving
            target.firstMonthSaving = newTarget.firstMonthSaving
            target.reductionFactor = newTarget.reductionFactor
        }
        realmAPI.updateTarget(newTarget)
    }

}
