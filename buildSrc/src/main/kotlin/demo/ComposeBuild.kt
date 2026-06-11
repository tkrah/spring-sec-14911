package demo

import org.gradle.api.GradleException
import java.util.concurrent.Semaphore

class ComposeBuild {
    var taskPath: String? = null

    var env: Map<String, String> = mapOf()

    val semaphore: Semaphore = Semaphore(1)

    fun acquire(
        taskPath: String,
        env: Map<String, String>,
    ) {
        semaphore.acquire()
        if (this.taskPath != null) {
            throw GradleException("Invalid build state!")
        }
        this.taskPath = taskPath
        if (!this.env.isEmpty()) {
            throw GradleException("Invalid build state!")
        }
        this.env = env
    }

    fun release() {
        taskPath = null
        env = mapOf()
        semaphore.release()
    }
}
