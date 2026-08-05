import com.google.protobuf.gradle.id

plugins {
    kotlin("jvm")
    alias(libs.plugins.protobuf)
}

group = "com.adventistportal"
version = "0.0.1-SNAPSHOT"

base { archivesName.set("contracts") }

repositories { mavenCentral() }

dependencies {
    api(libs.protobuf.java)
    api(libs.protobuf.kotlin)
    // The generated service stubs live here with the messages: one module owns the wire.
    api(libs.grpc.protobuf)
    api(libs.grpc.stub)
    api(libs.annotation.api)
}

protobuf {
    protoc { artifact = libs.protobuf.protoc.get().toString() }
    plugins {
        id("grpc") { artifact = libs.grpc.codegen.get().toString() }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins { id("kotlin") }
            task.plugins { id("grpc") }
        }
    }
}

sourceSets {
    main {
        proto {
            // Not the module root: protoc extracts the well-known types into build/,
            // and a source directory that contains its own output makes Gradle refuse
            // the task graph. buf reads the same tree through `path: proto`.
            srcDir("proto")
        }
    }
}

kotlin { jvmToolchain(21) }
