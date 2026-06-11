package demo

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import javax.inject.Inject

abstract class TaskSemaphoreExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        val taskSemaphore: Provider<TaskSemaphore>,
    )
