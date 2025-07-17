package com.example.moneyeverydayxml.core.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class TransactionTest {

    @Test
    fun `Transaction should be created with correct properties`() {
        // Arrange
        val id = 1L
        val time = 1640995200000L // 1 января 2022
        val date = "01 янв, суббота, 00:00"
        val count = "100.50"
        val description = "Тестовая транзакция"

        // Act
        val transaction = Transaction(
            id = id,
            time = time,
            date = date,
            count = count,
            description = description
        )

        // Assert
        assertEquals(id, transaction.id)
        assertEquals(time, transaction.time)
        assertEquals(date, transaction.date)
        assertEquals(count, transaction.count)
        assertEquals(description, transaction.description)
    }

    @Test
    fun `Transaction with null id should be created successfully`() {
        // Arrange
        val time = 1640995200000L
        val date = "01 янв, суббота, 00:00"
        val count = "50.25"
        val description = "Новая транзакция"

        // Act
        val transaction = Transaction(
            id = null,
            time = time,
            date = date,
            count = count,
            description = description
        )

        // Assert
        assertEquals(null, transaction.id)
        assertEquals(time, transaction.time)
        assertEquals(date, transaction.date)
        assertEquals(count, transaction.count)
        assertEquals(description, transaction.description)
    }

    @Test
    fun `Transaction copy should create new instance with updated values`() {
        // Arrange
        val originalTransaction = Transaction(
            id = 1L,
            time = 1640995200000L,
            date = "01 янв, суббота, 00:00",
            count = "100.00",
            description = "Оригинальная транзакция"
        )

        // Act
        val copiedTransaction = originalTransaction.copy(
            count = "150.00",
            description = "Обновленная транзакция"
        )

        // Assert
        assertEquals(originalTransaction.id, copiedTransaction.id)
        assertEquals(originalTransaction.time, copiedTransaction.time)
        assertEquals(originalTransaction.date, copiedTransaction.date)
        assertEquals("150.00", copiedTransaction.count)
        assertEquals("Обновленная транзакция", copiedTransaction.description)
        
        // Оригинальный объект не должен измениться
        assertEquals("100.00", originalTransaction.count)
        assertEquals("Оригинальная транзакция", originalTransaction.description)
    }

    @Test
    fun `Transaction should have correct string representation`() {
        // Arrange
        val transaction = Transaction(
            id = 1L,
            time = 1640995200000L,
            date = "01 янв, суббота, 00:00",
            count = "75.50",
            description = "Покупка продуктов"
        )

        // Act
        val stringRepresentation = transaction.toString()

        // Assert
        assertNotNull(stringRepresentation)
        assert(stringRepresentation.contains("75.50"))
        assert(stringRepresentation.contains("Покупка продуктов"))
    }
} 