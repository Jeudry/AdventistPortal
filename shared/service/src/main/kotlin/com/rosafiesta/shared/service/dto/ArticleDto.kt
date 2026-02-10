package com.rosafiesta.shared.service.dto

import com.rosafiesta.shared.domain.inventory.validation.ArticleValidationUtils
import com.rosafiesta.shared.domain.validation.ValidatableDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** DTO for article data transfer, shared in KMP. */
@Serializable
data class ArticleDto(
  /** Name of the article. */
  @SerialName("name")
  val name: String,
  /** Code of the article. */
  @SerialName("code")
  val code: String,
) : ValidatableDto {
  
  /** Validates all fields of this ArticleDto and returns a list of error messages.
   * @return List of error messages; empty if valid.
   */
  override fun validate(): List<String> {
    val errors = mutableListOf<String>()
    if (!ArticleValidationUtils.isNameValid(name)) errors.add("Article name cannot be blank")
    if (!ArticleValidationUtils.isCodeValid(code)) errors.add("Invalid article code: must be ${ArticleValidationUtils.CODE_MIN_LENGTH}-${ArticleValidationUtils.CODE_MAX_LENGTH} alphanumeric characters")
    return errors
  }
}