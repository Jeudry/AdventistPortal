package com.rosafiesta.shared.domain.validation

/// <summary>Interface for objects that can be validated, returning a list of error messages.</summary>
interface ValidatableDto {
    /// <summary>Validates the object and returns a list of error messages. Empty if valid.</summary>
    fun validate(): List<String>
}