description = "対戦型領土取得戦略ゲームプラグイン"
group = "jp.trap"
version = "1.0.0"
val mainClass = "jp.trap.conqest.Main"
val author = "traP"

plugins {
    kotlin("jvm") version libs.versions.kotlin
}

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly(libs.paperApi)
}

kotlin {
    jvmToolchain(21)
}

tasks.processResources {
    expand(
        mapOf(
            "version" to project.version,
            "mainClass" to mainClass,
            "description" to project.description,
            "author" to author,
            "apiVersion" to libs.versions.apiVersion.get()
        )
    )
}

tasks.jar {
    archiveFileName.set("conQest.jar")
}