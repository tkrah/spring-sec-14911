plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

kotlin {
    jvmToolchain(25)
}

gradlePlugin {
    plugins {
        register("demo.task-semaphore-plugin") {
            implementationClass = "demo.TaskSemaphorePlugin"
        }
    }
}
