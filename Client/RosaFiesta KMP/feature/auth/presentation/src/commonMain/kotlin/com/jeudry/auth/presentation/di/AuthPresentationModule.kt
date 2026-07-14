package com.adventistportal.auth.presentation.di

import com.adventistportal.auth.presentation.email_verification.EmailVerificationViewModel
import com.adventistportal.auth.presentation.forgot_password.ForgotPasswordViewModel
import com.adventistportal.auth.presentation.login.LoginViewModel
import com.adventistportal.auth.presentation.register.RegisterViewModel
import com.adventistportal.auth.presentation.register_success.RegisterSuccessViewModel
import com.adventistportal.auth.presentation.reset_password.ResetPasswordViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authPresentationModule = module {
    viewModelOf(::RegisterViewModel)
    viewModelOf(::RegisterSuccessViewModel)
    viewModelOf(::EmailVerificationViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::ForgotPasswordViewModel)
    viewModelOf(::ResetPasswordViewModel)
}