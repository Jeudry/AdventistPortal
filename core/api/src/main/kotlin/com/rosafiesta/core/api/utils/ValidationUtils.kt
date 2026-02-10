package com.rosafiesta.core.api.utils

import com.rosafiesta.shared.domain.validation.ValidatableDto

/** Utility functions for API responses and validations. */
object ValidationUtils {

    /** Handles validation errors by returning a bad request response if errors exist, otherwise null.
     * @param validatable The object to validate.
     * @return ResponseEntity with a bad request if errors are present, null otherwise.
     */
    fun handleErrors(validatable: ValidatableDto) {
        val errors = validatable.validate()
        if (errors.isNotEmpty()) {
            throw IllegalArgumentException("Validation failed: ${errors.joinToString("; ")}")
        }
    }
}