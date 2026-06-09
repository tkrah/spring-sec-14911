import org.gradle.kotlin.dsl.invoke

val taskSemaphore: Provider<BuildService<BuildServiceParameters.None>> by rootProject.extra

val composeUp =
    project.rootProject.tasks.named("composeUp") {
        outputs.upToDateWhen { false }
        usesService(taskSemaphore)
    }
val composeDown =
    project.rootProject.tasks.named("composeDown") {
        outputs.upToDateWhen { false }
        usesService(taskSemaphore)
    }

val runTest =
    tasks.register<Exec>("runTest") {
        commandLine = listOf("echo", "runTest")
        usesService(taskSemaphore)
    }

val integrationTest =
    tasks.register("integrationTest") {
        dependsOn(composeUp, runTest)
        usesService(taskSemaphore)
    }

composeUp {
    finalizedBy(composeDown)
}
composeDown {
    mustRunAfter(integrationTest)
}
runTest {
    mustRunAfter(composeUp)
}
