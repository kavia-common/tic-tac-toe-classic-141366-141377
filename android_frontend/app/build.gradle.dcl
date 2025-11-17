androidApplication {
    namespace = "org.example.app"

    testing {
        // Ensure JUnit 5 (Jupiter) tests are discovered and do not fail when empty
        failOnNoDiscoveredTests = false
        dependencies {
            implementation("org.junit.jupiter:junit-jupiter:5.10.2")
            runtimeOnly("org.junit.platform:junit-platform-launcher")
        }
    }

    dependencies {
        implementation("androidx.appcompat:appcompat:1.7.0")
        implementation("com.google.android.material:material:1.12.0")
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
        implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.8.6")
        implementation(project(":utilities"))
    }
}
