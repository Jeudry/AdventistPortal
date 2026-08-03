package com.adventistportal.user.domain.exception

import java.lang.RuntimeException

class UserAlreadyExistsEx: RuntimeException("User already exists")
