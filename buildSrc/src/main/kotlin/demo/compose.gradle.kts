package demo

tasks.register<Exec>("composeUp") {
    commandLine = listOf("echo", "composeUp")
}

tasks.register<Exec>("composeDown") {
    commandLine = listOf("echo", "composeDown")
}

tasks.register<Exec>("e2eComposeUp") {
    commandLine = listOf("echo", "e2eComposeUp")
}

tasks.register<Exec>("e2eComposeDown") {
    commandLine = listOf("echo", "e2eComposeDown")
}
