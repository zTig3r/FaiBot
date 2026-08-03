plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.2"
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
}

tasks.withType(JavaCompile::class.java) {
    options.encoding = "UTF-8"
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