import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.5.0"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}
val ktor_version = "2.3.5"
kotlin {
    jvm {
        jvmToolchain(17)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("androidx.compose.material3:material3:1.1.1")
                // Compose
//                    implementation("org.jetbrains.compose.ui:ui-test-junit4")
                implementation("org.junit.jupiter:junit-jupiter:5.7.0")
                implementation("org.junit.vintage:junit-vintage-engine:5.7.0")

                implementation("io.ktor:ktor-client-content-negotiation:2.3.5")
                implementation("io.ktor:ktor-serialization-jackson:2.3.5")
                implementation("io.ktor:ktor-client-okhttp:$ktor_version")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

            }
        }
        val jvmTest by getting{
            dependencies{
                dependencies {
                    implementation(kotlin("test"))
                    implementation(kotlin("test-common"))
                    implementation(kotlin("test-annotations-common"))
                    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
                    implementation("io.mockk:mockk:1.12.5")
                    // kotest
                    implementation("io.kotest:kotest-runner-junit5:5.3.1")
                    implementation("io.kotest:kotest-assertions-core:5.3.1")
                    implementation("io.kotest:kotest-property:5.3.1")
                    // ktor


                }
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "calculator"
            packageVersion = "1.0.1"
            windows {
                iconFile.set(project.file("icon.ico"))
            }
        }
    }
}

