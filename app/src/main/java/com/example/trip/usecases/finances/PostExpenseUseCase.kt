package com.example.trip.usecases.finances

import com.example.trip.dto.ExpensePostDto
import com.example.trip.models.Resource
import com.example.trip.repositories.FinancesRepository
import retrofit2.HttpException

import javax.inject.Inject

class PostExpenseUseCase @Inject constructor(private val financesRepository: FinancesRepository) {

    suspend operator fun invoke(groupId: Long, expensePostDto: ExpensePostDto): Resource<Unit> {
        return try {
            financesRepository.postExpense(groupId, expensePostDto)
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