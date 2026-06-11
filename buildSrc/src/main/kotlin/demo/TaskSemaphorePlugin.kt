package demo

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.build.event.BuildEventsListenerRegistry
import javax.inject.Inject

class TaskSemaphorePlugin : Plugin<Project> {
    val eventRegistry: BuildEventsListenerRegistry

    @Inject
    constructor(eventRegistry: BuildEventsListenerRegistry) {
        this.eventRegistry = eventRegistry
    }

    override fun apply(project: Project) {
        val serviceProvider: Provider<TaskSemaphore> =
            project.gradle.sharedServices.registerIfAbsent(
                "taskSemaphore",
                TaskSemaphore::class.java,
                {
                    parameters.getEnvironment().set(mapOf())
                    maxParallelUsages.set(4)
                },
            )

        project.extensions.create(
            "taskSemaphoreExtension",
            TaskSemaphoreExtension::class.java,
            serviceProvider,
        )

        eventRegistry.onTaskCompletion(serviceProvider)
    }
}
