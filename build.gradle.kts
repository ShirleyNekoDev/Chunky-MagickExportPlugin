import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

plugins {
    kotlin("jvm") version "1.4.30"
    idea
}

group = "de.groovybyte.chunky"
version = "1.0"

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    maven(url = "https://repo.lemaik.de/")
}

dependencies {
    implementation(kotlin("stdlib", version = "1.4.30"))

    implementation("se.llbit:chunky-core:2.4.0-SNAPSHOT")

    implementation("no.tornado:tornadofx:1.7.20")
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }
    withType<Jar> {
        archiveFileName.set("${archiveBaseName.get()}.${archiveExtension.get()}")
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
            languageVersion = "1.4"
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
