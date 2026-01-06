package com.kttipay.payment.internal.validation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AmountValidatorTest {

    @Test
    fun `validate returns Valid for valid amount with two decimals`() {
        val result = AmountValidator.validate("10.00")
        assertTrue(result is ValidationResult.Valid)
        assertEquals("10.00", result.amount)
    }

    @Test
    fun `validate returns Valid for valid amount with one decimal`() {
        val result = AmountValidator.validate("10.5")
        assertTrue(result is ValidationResult.Valid)
        assertEquals("10.5", result.amount)
    }

    @Test
    fun `validate returns Valid for valid amount without decimals`() {
        val result = AmountValidator.validate("10")
        assertTrue(result is ValidationResult.Valid)
        assertEquals("10", result.amount)
    }

    @Test
    fun `validate returns Error for empty string`() {
        val result = AmountValidator.validate("")
        assertTrue(result is ValidationResult.Error)
        assertEquals("Amount cannot be empty", result.message)
    }

    @Test
    fun `validate returns Error for blank string`() {
        val result = AmountValidator.validate("   ")
        assertTrue(result is ValidationResult.Error)
        assertEquals("Amount cannot be empty", result.message)
    }

    @Test
    fun `validate returns Error for invalid format with three decimals`() {
        val result = AmountValidator.validate("10.999")
        assertTrue(result is ValidationResult.Error)
        assertTrue(result.message.contains("Invalid amount format"))
    }

    @Test
    fun `validate returns Error for negative amount`() {
        val result = AmountValidator.validate("-10.00")
        assertTrue(result is ValidationResult.Error)
        assertTrue(result.message.contains("Invalid amount format"))
    }

    @Test
    fun `validate returns Error for zero amount`() {
        val result = AmountValidator.validate("0.00")
        assertTrue(result is ValidationResult.Error)
        assertEquals("Amount must be greater than zero", result.message)
    }

    @Test
    fun `validate returns Error for non-numeric string`() {
        val result = AmountValidator.validate("abc")
        assertTrue(result is ValidationResult.Error)
        assertTrue(result.message.contains("Invalid amount format"))
    }

    @Test
    fun `validate returns Error for amount with letters`() {
        val result = AmountValidator.validate("10.00abc")
        assertTrue(result is ValidationResult.Error)
        assertTrue(result.message.contains("Invalid amount format"))
    }

    @Test
    fun `validateOrThrow throws for invalid amount`() {
        try {
            AmountValidator.validateOrThrow("invalid")
            assertFalse(true, "Should have thrown IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertEquals(e.message?.contains("Invalid amount format"), true)
        }
    }

    @Test
    fun `validateOrThrow returns amount for valid input`() {
        val result = AmountValidator.validateOrThrow("10.50")
        assertEquals("10.50", result)
    }

    @Test
    fun `validate returns Valid for very large amount`() {
        val result = AmountValidator.validate("999999999999.99")
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `validate returns Error for amount starting with decimal point`() {
        val result = AmountValidator.validate(".50")
        assertTrue(result is ValidationResult.Error)
        assertTrue(result.message.contains("Invalid amount format"))
    }
}
