plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.2"
    id("idea")
    id ("io.freefair.lombok") version "9.0.0"
}

group = "de.ztiger"
version = "4.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.twitch4j:twitch4j:1.26.0")
    implementation("net.dv8tion:JDA:6.4.2") {
        exclude(module = "opus-java")
    }
    implementation("ch.qos.logback:logback-classic:1.5.37")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.4.1")
    implementation("org.slf4j:slf4j-api:2.0.12")
    implementation("io.github.cdimascio:dotenv-java:3.0.0")
    implementation("com.vaadin.external.google:android-json:0.0.20131108.vaadin1")
    implementation("com.github.Carleslc.Simple-YAML:Simple-Yaml:1.8.4")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.6.2")
    implementation("com.sparkjava:spark-core:2.9.4")
    implementation("com.j256.ormlite:ormlite-core:6.1")
    implementation("com.j256.ormlite:ormlite-jdbc:6.1")
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.4")
}

val generatedLocalizationDir = layout.buildDirectory.dir("generated/sources/localization/java")

val localizationYamlPath: String = rootProject.file("../k8s-deployments/apps/faibot/config/de_DE.yml").absolutePath

val targetPackage = "de.ztiger.faibot.localization.keys"

val generateLocalizationClasses by tasks.registering(Exec::class) {
    group = "build setup"
    description = "Generates Java localization classes from external YAML"

    val outputPackageDir = generatedLocalizationDir.get().dir(targetPackage.replace('.', '/'))

    inputs.file(localizationYamlPath)
    outputs.dir(outputPackageDir)

    commandLine(
        "python",
        file("scripts/generate_localization_keys.py").absolutePath,
        localizationYamlPath,
        outputPackageDir.asFile.absolutePath,
        targetPackage
    )
}

sourceSets {
    main {
        java {
            srcDir(generatedLocalizationDir)
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    dependsOn(generateLocalizationClasses)
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        manifest {
            attributes["Main-Class"] = "de.ztiger.faibot.FaiBot"
        }
    }
}

tasks.test {
    useJUnitPlatform()
}