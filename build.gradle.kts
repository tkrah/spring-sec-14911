plugins {
    java
    id("org.springframework.boot") version "4.0.6"
    id("demo.compose")
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

interface TaskSemaphore : BuildService<BuildServiceParameters.None>

val taskSemaphore =
    project.gradle.sharedServices.registerIfAbsent(
        "taskSemaphore",
        TaskSemaphore::class,
    ) {
        maxParallelUsages = 1
    }

extra.apply {
    set("taskSemaphore", taskSemaphore)
}
