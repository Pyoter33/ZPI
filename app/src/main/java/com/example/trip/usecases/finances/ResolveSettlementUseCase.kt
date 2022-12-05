package com.example.trip.usecases.finances

import com.example.trip.models.Resource
import com.example.trip.repositories.FinancesRepository
import retrofit2.HttpException

import javax.inject.Inject

class ResolveSettlementUseCase @Inject constructor(private val financesRepository: FinancesRepository) {

    suspend operator fun invoke(settlementId: Long): Resource<Unit> {
        return try {
            financesRepository.resolveSettlement(settlementId)
            Resource.Success(Unit)
        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Failure(e.code())
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(0)
        }
    }
}