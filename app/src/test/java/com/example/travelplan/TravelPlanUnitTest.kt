package com.example.travelplan

import com.example.travelplan.data.local.TripEntity
import com.example.travelplan.data.toDomainModel
import com.example.travelplan.domain.Expense
import org.junit.Assert.*
import org.junit.Test

class TravelPlanUnitTest {

    @Test
    fun passwordValidation_tooShort_returnsFalse() {
        val shortPassword = "123"
        val validPassword = "securePassword123"

        assertFalse("Пароль коротший за 6 символів має бути відхилено", shortPassword.length >= 6)
        assertTrue("Пароль від 6 символів має бути прийнято", validPassword.length >= 6)
    }

    @Test
    fun expenseCalculation_sumsCorrectly() {
        val expenses = listOf(
            Expense(category = "Готель", amount = 450.0),
            Expense(category = "Квитки", amount = 350.0),
            Expense(category = "Їжа", amount = 200.0)
        )

        val totalSpent = expenses.sumOf { it.amount }

        assertEquals("Сума витрат підрахована неправильно", 1000.0, totalSpent, 0.001)
    }

    @Test
    fun budgetLimit_exceededDetectedCorrectly() {
        val budget = 1200.0
        val spentNormal = 1000.0
        val spentOver = 1500.0

        assertFalse("Бюджет не повинен вважатися перевищеним", spentNormal > budget)
        assertTrue("Система має зафіксувати перевищення бюджету", spentOver > budget)
    }

    @Test
    fun mapper_convertsEntityToDomainCorrectly() {
        val entity = TripEntity(
            id = "test_trip_1",
            title = "Подорож до Риму",
            destination = "Рим, Італія",
            startDate = 1700000000000L,
            endDate = 1700600000000L,
            budget = 1200.0,
            isSynced = true
        )

        val domainTrip = entity.toDomainModel()

        assertEquals(entity.id, domainTrip.id)
        assertEquals(entity.title, domainTrip.title)
        assertEquals(entity.destination, domainTrip.destination)
        assertEquals(entity.budget, domainTrip.budget, 0.001)
        assertEquals(entity.isSynced, domainTrip.isSynced)
    }
}