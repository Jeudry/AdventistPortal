plugins {
    id("rosafiesta.service")
}

dependencies {
    implementation(projects.features.quotes.domain)
    implementation(projects.features.inventory.domain)
    implementation(projects.shared.domain) // Para QuoteStatus
    implementation(projects.core.service)
    
    // Spring dependencies necesarias para @Service y @Transactional
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.data.jpa) // Para @Transactional
}