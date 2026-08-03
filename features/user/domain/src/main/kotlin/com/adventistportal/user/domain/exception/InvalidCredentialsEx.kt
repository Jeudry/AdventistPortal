package com.adventistportal.user.domain.exception

import java.lang.RuntimeException

class InvalidCredentialsEx: RuntimeException("The provided credentials are invalid")