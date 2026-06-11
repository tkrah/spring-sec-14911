import demo.TaskSemaphore

val taskSemaphore =
    project.gradle.sharedServices.registerIfAbsent(
        "taskSemaphore",
        TaskSemaphore::class,
    ) {
        maxParallelUsages = 1
    }

val integrationTest =
    tasks.register<Exec>("integrationTest") {
        val ts = taskSemaphore
        doFirst {
            ts.get().startUp("B", mapOf("PORT" to "5435"))
        }
        commandLine = listOf("echo", "B")
        usesService(taskSemaphore)
    }
