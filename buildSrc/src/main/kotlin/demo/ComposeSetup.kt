package demo

import org.gradle.api.provider.MapProperty
import org.gradle.api.services.BuildServiceParameters

interface ComposeSetup : BuildServiceParameters {
    fun getEnvironment(): MapProperty<String, String>
}
