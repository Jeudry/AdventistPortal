package com.adventistportal.user.domain.exception

class SamePasswordEx: RuntimeException("The new password cannot be the same as the old password")