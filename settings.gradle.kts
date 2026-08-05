pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://repo.spring.io/snapshot") }
    }
}


// shared is its own multiplatform build, shared with the KMP client.
// Composite build: the declarations below resolve from source, not from a repository.
includeBuild("shared")

rootProject.name = "adventistportal-api"

include("app")


include("features:chat:domain")
include("features:chat:infra")
include("features:chat:service")
include("features:chat:api")

include("features:notification:domain")
include("features:notification:infra")
include("features:notification:service")
include("features:notification:api")

include("features:user:domain")
include("features:user:infra")
include("features:user:service")
include("features:user:api")

include("features:inventory:domain")
include("features:inventory:infra")
include("features:inventory:service")
include("features:inventory:api")


include("contracts")
include("core:domain")
include("core:infra")
include("core:service")
include("core:api")

include("services:notification")
include("services:gateway")
