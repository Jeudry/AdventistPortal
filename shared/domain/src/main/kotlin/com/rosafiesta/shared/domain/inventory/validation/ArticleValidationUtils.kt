package com.rosafiesta.shared.domain.inventory.validation

/** Utility class for shared validation logic related to inventory. */
object ArticleValidationUtils {
    /** Minimum length for the article code. */
    const val CODE_MIN_LENGTH = 3
    /** Maximum length for the article code. */
    const val CODE_MAX_LENGTH = 10

    /** Validates if the article name is not blank.
     * @param name The name to validate.
     * @return True if valid, false otherwise.
     */
    fun isNameValid(name: String): Boolean = name.isNotBlank()

    /** Validates if the article code meets the requirements.
     * @param code The code to validate.
     * @return True if valid, false otherwise.
     */
    fun isCodeValid(code: String): Boolean {
        val lengthValid = code.length in CODE_MIN_LENGTH..CODE_MAX_LENGTH
        val formatValid = code.all { it.isLetterOrDigit() }
        return lengthValid && formatValid
    }
}