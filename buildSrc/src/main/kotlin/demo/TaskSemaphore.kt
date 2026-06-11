package demo

import org.gradle.api.GradleException
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.services.BuildService
import org.gradle.tooling.events.FinishEvent
import org.gradle.tooling.events.OperationCompletionListener
import org.gradle.tooling.events.task.TaskFinishEvent
import javax.inject.Inject

abstract class TaskSemaphore :
    BuildService<ComposeSetup>,
    OperationCompletionListener,
    AutoCloseable {
    var lastStarted: String? = null

    val providerFactory: ProviderFactory

    @Inject
    constructor(providerFactory: ProviderFactory) {
        this.providerFactory = providerFactory
    }

    override fun onFinish(event: FinishEvent?) {
        println("Handle event: $event")
        if (event is TaskFinishEvent) {
            tearDown(event)
        }
    }

    fun startUp(
        composeSetup: String,
        env: Map<String, String>,
    ) {
        if (lastStarted != null) {
            throw GradleException("tearDown not called!")
        }
        println("Starting up $composeSetup with env: $env")

        // how to get the working directory here from the project ...
        val exec =
            providerFactory.exec {
                environment(env)
                commandLine("bash", "env.sh")
            }
        val text = exec.standardOutput.asText.get()
        println("Compose Output: $text")

        lastStarted = composeSetup
    }

    fun tearDown(event: TaskFinishEvent?) {
        if (lastStarted != null) {
            println("TearDown $lastStarted after $event?.displayName")
        }
        lastStarted = null
    }

    override fun close() {
        tearDown(null)
    }
}
