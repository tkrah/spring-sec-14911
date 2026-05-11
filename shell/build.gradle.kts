import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    alias(libs.plugins.spring.boot)
    java
}

dependencies {
    implementation(libs.liquibase)
    implementation(platform(libs.spring.shell))
    implementation("org.springframework.shell:spring-shell-starter")
    implementation(platform(SpringBootPlugin.BOM_COORDINATES))
    implementation(platform(libs.spring.shell))
    implementation(project(":server"))
    implementation("org.springframework.shell:spring-shell-starter-jansi")

    testImplementation(project(":server"))
}
