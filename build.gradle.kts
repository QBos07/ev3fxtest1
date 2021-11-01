plugins {
    kotlin("jvm") version "1.5.31"
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("org.openjfx.javafxplugin") version "0.0.8"
    application
}
group = "com.test"
version = "1.0-SNAPSHOT"

val tornadofxVersion: String by rootProject

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.example.MainKt")
}

dependencies {
    implementation(kotlin("stdlib-jdk8", "1.5.31"))
    implementation(kotlin("reflect", "1.5.31"))
    implementation("no.tornado:tornadofx:$tornadofxVersion")
    implementation(files("../lejos-ev3/lib/ev3/ev3classes.jar", "../lejos-ev3/lib/ev3/dbusjava.jar", "../lejos-ev3/lib/ev3/3rdparty/opencv-2411.jar"))
    testImplementation(kotlin("test-testng", "1.5.31"))
}

javafx {
    modules = listOf("javafx.controls", "javafx.graphics")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    compileJava{
        version = "11"
    }
    compileTestJava{
        version = "11"
    }
    wrapper {
        gradleVersion = "7.2"
    }
}