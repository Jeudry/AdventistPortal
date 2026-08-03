package com.adventistportal.core.domain.util

class DataErrorException(
    val error: DataError
): Exception()