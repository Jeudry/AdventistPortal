plugins {
    alias(libs.plugins.convention.android.application.compose)
    alias(libs.plugins.google.services)
}

dependencies {
    // Shared Compose Multiplatform UI (KMP library, Android variant).
    implementation(projects.composeApp)

    implementation(libs.androidx.activity.compose)
    implementation(libs.core.splashscreen)
    implementation(libs.koin.android)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui.tooling.preview)
}
