plugins {
    kotlin("jvm") version "1.9.24"
    id("org.jetbrains.compose") version "1.6.11"
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material)
    implementation(compose.foundation)
    implementation(compose.runtime)
    implementation(compose.ui)
}

compose.desktop {
    application {
        mainClass = "org.example.project.MainKt"
    }
}