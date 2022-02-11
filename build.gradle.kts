import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel
import org.gradle.plugins.ide.idea.model.Module
import org.gradle.plugins.ide.idea.model.SingleEntryModuleLibrary

plugins {
    kotlin("jvm") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.0"
    //id("org.openjfx.javafxplugin") version "0.0.10"
    application
    //checkstyle
    id("org.jetbrains.dokka") version "1.6.10"
    //idea
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
    implementation(files("../lejos-ev3/lib/ev3/ev3classes.jar", "../lejos-ev3/lib/ev3/dbusjava.jar", "/jna/dist/jna.jar"))
    implementation("org.jetbrains.kotlinx:kotlinx-cli-jvm:0.3.4")
    testImplementation(kotlin("test-testng", kotlin.coreLibrariesVersion))
    testImplementation("org.powermock:powermock-module-testng:2.0.9")
    testImplementation("org.powermock:powermock-api-mockito2:2.0.9")
    testImplementation("org.mockito:mockito-core:4.3.1")
    constraints {
        implementation(kotlin("reflect", kotlin.coreLibrariesVersion))
    }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))

/*javafx {
    modules = listOf("javafx.controls", "javafx.graphics")
}*/

tasks.wrapper.get().gradleVersion = "7.4"
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.ExperimentalUnsignedTypes"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    tasks.compileTestKotlin.get().kotlinOptions.jvmTarget = "1.8"
}
//tasks.withType<JavaCompile>().configureEach { options.release.set(8) }
/*
idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
        jdkName = "temurin-1.8"
        languageLevel = IdeaLanguageLevel(8)
        targetVersion = "8"
        targetBytecodeVersion = JavaVersion.VERSION_1_8
        iml {
            whenMerged(Action<Module> {
                dependencies.forEach { (it as SingleEntryModuleLibrary).run {
                    //println("${this.libraryFile} : ${this.javadocFile} : ${this.sourceFile}")
                    if(this.libraryFile.path.contains("ev3classes.jar")) {
                        this.sourceFile = File(this.libraryFile.path.replace(".jar", "-src.zip"))
                    }
                }
                }
            })

            //singleEntryLibraries.forEach { (k, v) -> println("$k -> ${v.joinToString(separator = " ", transform = {it.path})}") }
        }

    }
    project {
        jdkName = "temurin-1.8"
        languageLevel = IdeaLanguageLevel(8)
        targetVersion = "8"
        targetBytecodeVersion = JavaVersion.VERSION_1_8
        vcs = "GitHub"
        //projectLibraries.forEach { println("${it.name} : ${it.type} : ${it.classes} : ${it.javadoc} : ${it.sources}") }
    }
}
*/