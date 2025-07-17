package com.example.moneyeverydayxml

import com.google.common.truth.Truth.assertThat
import org.junit.Test

internal class CalculatorTest {
    private val calculator = Calculator()

    @Test
    fun `5 plus 3 equals 8`() {
//arrange
        val firstNumber = 5
        val secondNumber = 3
        val expected = 8
//act
        val actual = calculator.calculateTwoNumbers(firstNumber, secondNumber)

        //assert
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `5 plus 3 is not equals 10`() {

        val first = 5
        val second = 3
        val expected = 10

        val actual = calculator.calculateTwoNumbers(first, second)

        assertThat(actual).isNotEqualTo(expected)
    }
}