plugins {
    kotlin("jvm") version "1.5.31"
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("org.openjfx.javafxplugin") version "0.0.8"
    application
    //checkstyle
    id("org.jetbrains.dokka") version "1.6.0"
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
    //testImplementation(kotlin("test-testng", "1.5.31"))
    constraints {
        implementation(kotlin("reflect", "1.5.31"))
    }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(11))

javafx {
    modules = listOf("javafx.controls", "javafx.graphics")
}

tasks.wrapper.get().gradleVersion = "7.3"
tasks.compileKotlin.get().kotlinOptions.jvmTarget = "1.8"
tasks.compileTestKotlin.get().kotlinOptions.jvmTarget = "1.8"
tasks.compileJava.get().options.release.set(8)
tasks.compileTestJava.get().options.release.set(8)
@kotlin.Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
tasks.run.get().args!!.add("--dev-mode")