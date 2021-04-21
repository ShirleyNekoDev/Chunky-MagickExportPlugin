import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

plugins {
    kotlin("jvm") version "1.5.0-RC"
    id("application")

    id("com.github.ben-manes.versions") version "0.38.0"
    idea
}

group = "de.groovybyte.chunky"
version = "1.1"

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    maven(url = "https://repo.lemaik.de/")
}

dependencies {
    val kotlinVersion = "1.5.0-RC"
    implementation(kotlin("stdlib-jdk8", version = kotlinVersion))

    implementation("se.llbit:chunky-core:2.4.0-SNAPSHOT") {
        isChanging = true
    }

    implementation("no.tornado:tornadofx:1.7.20") {
        constraints {
            implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
        }
    }
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }
    withType<Jar> {
        archiveFileName.set("${archiveBaseName.get()}.${archiveExtension.get()}")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        configurations["compileClasspath"].apply {
            files { dep ->
                when {
                    dep.name.startsWith("chunky") -> false
                    else -> true
                }
            }.forEach { file ->
                from(zipTree(file.absoluteFile))
            }
        }
    }

    withType<KotlinJvmCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            languageVersion = "1.5"
            useIR = true
        }
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}
