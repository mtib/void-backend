import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    kotlin("jvm") version "1.9.23"
    id("com.gradleup.shadow") version "8.3.0"
}

group = "dev.mtib"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

task("fatJar", ShadowJar::class) {
    archivesName.set("void-backend")
    archiveClassifier.set("")
    archiveVersion.set("")
    manifest {
        attributes["Main-Class"] = "dev.mtib.void.api.VoidApiKt"
    }
    from(sourceSets["main"].output)
}