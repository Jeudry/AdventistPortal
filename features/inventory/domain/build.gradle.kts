plugins {
    id("adventistportal.domain")
}

group = "com.adventistportal.inventory"
version = "0.0.1-SNAPSHOT"

base {
    archivesName.set("inventory-domain")
}
dependencies {
    implementation("com.adventistportal.shared:domain:1.0.0")
}