import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

plugins {
    kotlin("jvm") version "1.6.0"
    id("org.openjfx.javafxplugin") version "0.0.10"

    id("com.github.ben-manes.versions") version "0.39.0"
    idea
}

group = "de.groovybyte.chunky"
version = "1.1"
val chunkyVersion = "2.4.1"

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    maven(url = "https://repo.lemaik.de/")
}

dependencies {
    val kotlinVersion = "1.6.0"
    implementation(kotlin("stdlib-jdk8", version = kotlinVersion))

    implementation("se.llbit:chunky-core:$chunkyVersion") {
        isChanging = true
    }

    implementation("no.tornado:tornadofx:1.7.20") {
        constraints {
            implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
        }
    }
}

javafx {
    version = "17"
    modules = listOf("javafx.controls", "javafx.fxml")
}

tasks {
    processResources {
        expand(
            "version" to project.version,
            "chunkyVersion" to chunkyVersion,
        )
    }

    withType<KotlinJvmCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            apiVersion = "1.6"
            languageVersion = "1.6"
        }
    }

    withType<Jar> {
        archiveFileName.set("${archiveBaseName.get()}.${archiveExtension.get()}")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        configurations["compileClasspath"].apply {
            files { dep ->
                when {
                    dep.name.startsWith("chunky") -> false
                    dep.name.startsWith("javafx") -> false
                    else -> true
                }
            }.forEach { file ->
                from(zipTree(file.absoluteFile))
            }
        }
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}
