@file:Suppress("GradlePackageUpdate")
plugins {
    kotlin("jvm") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("org.openjfx.javafxplugin") version "0.0.10"
    application
    //checkstyle
    id("org.jetbrains.dokka") version "1.6.10"
}
group = "qbos.ev3fx.test1"
version = "1.0-SNAPSHOT"

val tornadofxVersion: String by rootProject
val mainClassString: String by rootProject

repositories {
    mavenCentral()
}

application {
    mainClass.set(mainClassString)
}

dependencies {
    implementation("no.tornado:tornadofx:$tornadofxVersion")
    implementation(files("../lejos-ev3/lib/ev3/ev3classes.jar", "../lejos-ev3/lib/ev3/dbusjava.jar", "../lejos-ev3/lib/ev3/3rdparty/opencv-2411.jar"))
    implementation("org.jetbrains.kotlinx:kotlinx-cli-jvm:0.3.4")
    testImplementation(kotlin("test-testng", kotlin.coreLibrariesVersion))
    testImplementation("org.powermock:powermock-module-testng:2.+")
    testImplementation("org.powermock:powermock-api-mockito2:2.+")
    testImplementation("org.mockito:mockito-core:4.+")
    constraints {
        implementation(kotlin("reflect", kotlin.coreLibrariesVersion))
    }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(11))

javafx {
    modules = listOf("javafx.controls", "javafx.graphics")
}

tasks.wrapper.get().gradleVersion = "7.3"
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.ExperimentalUnsignedTypes"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    tasks.compileTestKotlin.get().kotlinOptions.jvmTarget = "1.8"
}
tasks.withType<JavaCompile>().configureEach { options.release.set(8) }