package demo

import org.gradle.api.artifacts.ComponentMetadataContext
import org.gradle.api.artifacts.ComponentMetadataRule

// https://docs.gradle.org/current/userguide/dependency_version_alignment.html#sec:virtual_platform
abstract class TomcatAlignmentRule : ComponentMetadataRule {
    override fun execute(ctx: ComponentMetadataContext) {
        ctx.details.run {
            if (id.group.startsWith("org.apache.tomcat.embed")) {
                belongsTo("org.apache.tomcat.embed:tomcat-virtual-platform:${id.version}")
            }
        }
    }
}
