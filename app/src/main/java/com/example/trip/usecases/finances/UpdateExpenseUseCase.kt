package com.example.trip.usecases.finances

import com.example.trip.dto.ExpensePostDto
import com.example.trip.models.Resource
import com.example.trip.repositories.FinancesRepository
import retrofit2.HttpException
import java.net.ConnectException
import javax.inject.Inject

class UpdateExpenseUseCase @Inject constructor(private val financesRepository: FinancesRepository) {

    suspend operator fun invoke(groupId: Long, expenseId: Long, expensePostDto: ExpensePostDto): Resource<Unit> {
        return try {
            financesRepository.updateExpense(groupId, expenseId, expensePostDto)
            Resource.Success(Unit)
        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Failure(e.code())
        } catch (e: ConnectException) {
            e.printStackTrace()
            Resource.Failure(0)
        }
    }
}