package com.cilo.app.domain.reduce

import com.cilo.app.data.TipTargetRepository
import com.cilo.app.data.models.Target
import com.cilo.app.data.models.Tip
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

class TipTargetUseCase(private val tipTargetsRepository: TipTargetRepository) {

    fun getTips(): List<Tip> = tipTargetsRepository.getTips()
    fun getTargets(): List<Target> = tipTargetsRepository.getTargets()
    suspend fun setTarget(target: Target) {
        tipTargetsRepository.setTarget(target)
    }

    suspend fun deleteTarget(target: Target) = tipTargetsRepository.deleteTarget(target)

    fun getTipById(id: BsonObjectId): Tip = tipTargetsRepository.getTipById(id)
    fun getTargetById(id: BsonObjectId): Target = tipTargetsRepository.getTargetById(id)
    fun getTargetByAssociatedTipId(tipId: ObjectId): List<Target> =
        tipTargetsRepository.getTargetByAssociatedTipId(tipId)

    suspend fun updateTarget(newTarget: Target) {
        tipTargetsRepository.updateTarget(newTarget)
    }

}