import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

plugins {
    kotlin("jvm") version "1.4.30"
    id("application")
    id("org.openjfx.javafxplugin") version "0.0.9"
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

    implementation(platform("com.fasterxml.jackson:jackson-bom:2.12.+"))
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    implementation("no.tornado:tornadofx:1.7.20")
}

javafx {
    version = "15.0.1"
    modules = listOf("javafx.controls", "javafx.fxml")
}
application {
    mainClass.set("de.groovybyte.chunky.magickexportplugin.MagickExportPluginKt")
}
tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
}

tasks.withType<KotlinJvmCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        languageVersion = "1.4"
        useIR = true
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}
