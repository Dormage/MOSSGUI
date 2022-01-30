import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    id("org.openjfx.javafxplugin")  version "0.0.10"
    application
}

group = "me.dormage"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}


dependencies{
    implementation("org.openjfx:javafx:17.0.2")
    javafx {
        modules("javafx.controls", "javafx.fxml", "javafx.media")
    }
    implementation("io.github.bitstorm:tinyzip-core:1.0.0")
    implementation("it.zielke:moji:1.0.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("Main")
}