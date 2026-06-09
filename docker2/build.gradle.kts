val taskSemaphore: Provider<BuildService<BuildServiceParameters.None>> by rootProject.extra

val composeUp =
    project.rootProject.tasks.named("e2eComposeUp") {
        outputs.upToDateWhen { false }
        usesService(taskSemaphore)
    }
val composeDown =
    project.rootProject.tasks.named("e2eComposeDown") {
        outputs.upToDateWhen { false }
        usesService(taskSemaphore)
    }

val runTest =
    tasks.register<Exec>("e2eRunTest") {
        commandLine = listOf("echo", "e2eRunTest")
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
