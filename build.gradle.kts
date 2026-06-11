plugins {
    java
    id("org.springframework.boot") version "4.1.0"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

subprojects {
    pluginManager.withPlugin("java") {
        java {
            toolchain {
                languageVersion = JavaLanguageVersion.of(25)
            }
        }
    }
}

if (gradle.startParameter.isBuildScan) {
    throw GradleException("Scanning disabled!")
}
