import demo.TomcatAlignmentRule
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    java
    war
    `jvm-test-suite`
    id("demo.task-semaphore-plugin")
    id("org.springframework.boot") version "4.1.0"
}

val mockitoAgent = configurations.create("mockitoAgent")

val tomcatVersion = providers.gradleProperty("tomcat_version").get()

dependencies {

    implementation(platform(SpringBootPlugin.BOM_COORDINATES))

    implementation("org.springframework.boot:spring-boot-configuration-processor")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-aspectj")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.springframework.boot:spring-boot-starter-integration")
    implementation("org.springframework.boot:spring-boot-starter-jackson")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-jms")
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-restclient")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webclient")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
    testImplementation("org.springframework.boot:spring-boot-starter-aspectj-test")
    testImplementation("org.springframework.boot:spring-boot-starter-cache-test")
    testImplementation("org.springframework.boot:spring-boot-starter-hateoas-test")
    testImplementation("org.springframework.boot:spring-boot-starter-integration-test")
    testImplementation("org.springframework.boot:spring-boot-starter-jackson-test")
    testImplementation("org.springframework.boot:spring-boot-starter-jdbc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-jms-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-oauth2-client-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server-test")
    testImplementation("org.springframework.boot:spring-boot-starter-restclient-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webclient-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")

    providedRuntime(platform(SpringBootPlugin.BOM_COORDINATES))
    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
    providedRuntime(libs.postgres.jdbc)

    mockitoAgent(libs.byte.buddy.agent)

    components.all<TomcatAlignmentRule>()

    constraints {
        implementation(enforcedPlatform("org.apache.tomcat.embed:tomcat-virtual-platform:$tomcatVersion"))
        providedRuntime(enforcedPlatform("org.apache.tomcat.embed:tomcat-virtual-platform:$tomcatVersion"))
    }
}

tasks.test {
    enabled = false
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

testing {
    suites {
        register<JvmTestSuite>("integrationTest") {
            targets {
                all {
                    testTask.configure {
                        outputs.dir("build/test-results/integrationTest")
                        outputs.dir("build/reports/tests/integrationTest")

                        environment("SPRING_PROFILES_ACTIVE" to "default")
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
                        jvmArgs("-javaagent:${mockitoAgent.asPath}")
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

taskSemaphoreExtension {
}

val testImplementation = configurations.getByName("testImplementation")
val integrationTestImplementation = configurations.getByName("integrationTestImplementation")
integrationTestImplementation.extendsFrom(testImplementation)

val integrationTest =
    tasks.named("integrationTest") {
        val ts = taskSemaphoreExtension.taskSemaphore
        doFirst {
            ts.get().startUp(
                "A",
                mapOf(
                    "COMPOSE_PROJECT_NAME" to "docker1",
                    "PG_TEST_PORT" to "5434",
                    "POSTGRES_IMAGE_VERSION" to "18-trixie",
                ),
                path,
            )
        }
        usesService(ts)
    }

tasks.withType<Test>().configureEach {
    jvmArgs(
        "-XX:MaxMetaspaceSize=384m",
        "-Xshare:off",
        "--sun-misc-unsafe-memory-access=allow",
        "--enable-native-access=ALL-UNNAMED",
    )
}
