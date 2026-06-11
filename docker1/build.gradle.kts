import demo.TaskSemaphore
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    java
    `jvm-test-suite`
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:6.0.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.0.3")
}

val taskSemaphore =
    project.gradle.sharedServices.registerIfAbsent(
        "taskSemaphore",
        TaskSemaphore::class,
    ) {
        maxParallelUsages = 1
    }

tasks.test {
    enabled = false
}

testing {
    suites {
        val integrationTest by registering(JvmTestSuite::class) {
            targets {
                all {
                    testTask.configure {
                        outputs.dir("build/test-results/integrationTest")
                        outputs.dir("build/reports/tests/integrationTest")

                        environment("PROFILES_ACTIVE" to "default")
                        filter {
                            includeTestsMatching("demo.integration.*")
                        }
                    }
                }
            }
        }
        withType(JvmTestSuite::class).matching { it.name in listOf("integrationTest") }.configureEach {
            useJUnitJupiter()
            dependencies {
                implementation(project())
            }
            sources {
                java {
                    setSrcDirs(listOf("src/test/java"))
                }
                resources {
                    setSrcDirs(listOf("src/test/resources"))
                }
            }
            targets {
                all {
                    testTask.configure {
                        inputs.dir("src/test")
                        minHeapSize = "1G"
                        maxHeapSize = "1G"
                        testLogging {
                            events = setOf(TestLogEvent.SKIPPED, TestLogEvent.FAILED)
                            showStandardStreams = false
                        }
                    }
                }
            }
        }
    }
}

val integrationTest =
    tasks.named("integrationTest") {
        val ts = taskSemaphore
        doFirst {
            ts.get().startUp("A", mapOf("PORT" to "5434"))
        }
        usesService(taskSemaphore)
    }
