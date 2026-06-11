package demo

import org.gradle.api.GradleException
import org.gradle.api.logging.Logger
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.services.BuildService
import org.gradle.process.ExecOutput
import org.gradle.tooling.events.FinishEvent
import org.gradle.tooling.events.OperationCompletionListener
import org.gradle.tooling.events.task.TaskFinishEvent
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import javax.inject.Inject
import kotlin.text.trim

abstract class TaskSemaphore :
    BuildService<ComposeSetup>,
    OperationCompletionListener {
    val taskMap: ConcurrentMap<String, ComposeBuild> = ConcurrentHashMap()

    val taskPaths: ConcurrentMap<String, String> = ConcurrentHashMap()

    val providerFactory: ProviderFactory

    val log: Logger

    @Inject
    constructor(providerFactory: ProviderFactory) {
        this.providerFactory = providerFactory
        this.log = LoggerFactory.getLogger("tasksemaphore-plugin") as Logger
    }

    fun getLogger(): Logger = this.log

    override fun onFinish(event: FinishEvent?) {
        if (event is TaskFinishEvent) {
            val values = taskPaths.values
            if (values.contains(event.descriptor.taskPath)) {
                getLogger().debug("Handle event: ${event.descriptor.taskPath}")
                taskPaths.remove(event.descriptor.taskPath)
                tearDown(event)
            }
        }
    }

    fun startUp(
        composeSetup: String,
        env: Map<String, String>,
        taskPath: String,
    ) {
        val composeBuild = ComposeBuild()
        val build = taskMap.computeIfAbsent(composeSetup) { composeBuild }
        build.acquire(taskPath, env)

        getLogger().lifecycle("Starting up $composeSetup with env: $env")

        taskPaths.putIfAbsent(taskPath, taskPath)

        val exec =
            providerFactory.exec {
                environment(env)
                commandLine("docker", "compose", "-f", "docker-compose.yml", "run", "--rm", "-d", "db-start")
                isIgnoreExitValue = true
            }

        val execResult = exec.result.get()
        logExecOutput(exec)
        execResult.assertNormalExitValue()
    }

    fun logExecOutput(exec: ExecOutput) {
        if (exec.result.isPresent) {
            val textErr = exec.standardError.asText.get()
            if (textErr.trim().isNotEmpty()) {
                getLogger().lifecycle("StdErr: \n$textErr")
            }
            val textOut = exec.standardOutput.asText.get()
            if (textOut.trim().isNotEmpty()) {
                getLogger().lifecycle("StdOut: \n$textOut")
            }
        } else {
            throw GradleException("Invalid state!")
        }
    }

    fun tearDown(event: TaskFinishEvent?) {
        val taskPath = event!!.descriptor!!.taskPath
        getLogger().lifecycle("TearDown $taskPath")

        val filter = taskMap.filter { (_, value) -> value.taskPath.equals(taskPath) }
        if (filter.size != 1) {
            throw GradleException("Invalid state!")
        }
        val build = filter.values.first()

        val exec =
            providerFactory.exec {
                environment(build.env)
                commandLine("docker", "compose", "-f", "docker-compose.yml", "down", "--rmi", "local")
            }
        try {
            exec.result.get().assertNormalExitValue()
        } finally {
            val text = exec.standardError.asText.get()
            if (text.trim().isNotEmpty()) {
                getLogger().lifecycle("Output: \n$text")
            }
        }

        build.release()
    }
}
