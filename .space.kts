job("Build and run tests") {
    container("openjdk:14") {
        kotlinScript { api ->
            api.gradlew("--no-daemon build")
        }
    }
}
