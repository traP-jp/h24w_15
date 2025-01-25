import java.net.URI

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
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.exposed:exposed-core:0.58.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.58.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.58.0")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

tasks.processResources {
    expand(
        mapOf(
            "version" to project.version,
            "mainClass" to mainClass,
            "description" to project.description,
            "author" to author,
            "apiVersion" to libs.versions.apiVersion.get(),
            "kotlinVersion" to libs.versions.kotlin.get()
        )
    )
}

tasks.jar {
    archiveFileName.set("conQest.jar")
}

tasks.register("createServer") {
    description = "Task for debugging"
    dependsOn(tasks.build)

    doLast {
        File(rootDir, "server/plugins").mkdirs()
        URI("https://api.papermc.io/v2/projects/paper/versions/1.21.4/builds/118/downloads/paper-1.21.4-118.jar")
            .toURL().openStream().use { input ->
                File("server/paper.jar").outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        File(rootDir, "build/libs/conQest.jar")
            .copyTo(File(rootDir, "server/plugins/conQest.jar"), overwrite = true)
        File("server/eula.txt").writeText("eula=true")
    }
}