androidApplication {
    namespace = "org.example.app"

    testing {
        // JUnit 5 dependencies for unit tests
        dependencies {
            implementation("org.junit.jupiter:junit-jupiter:5.10.2")
            runtimeOnly("org.junit.platform:junit-platform-launcher")
        }
        // Note: The experimental DCL plugin may not support 'options' here; relying on default test options.
    }

    dependencies {
        implementation("androidx.appcompat:appcompat:1.7.0")
        implementation("com.google.android.material:material:1.12.0")
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
        implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.8.6")
        implementation(project(":utilities"))
    }

}
