plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "de.ztiger"
version = "2.5"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.twitch4j:twitch4j:1.21.0")
    implementation("net.dv8tion:JDA:5.0.0-beta.24") {
        exclude(module="opus-java")
    }
    implementation ("ch.qos.logback:logback-classic:1.5.10")
    implementation ("org.mariadb.jdbc:mariadb-java-client:3.4.1")
    implementation ("org.slf4j:slf4j-api:2.0.12")
    implementation ("io.github.cdimascio:dotenv-java:3.0.0")
    implementation ("com.vaadin.external.google:android-json:0.0.20131108.vaadin1")
    implementation ("com.github.Carleslc.Simple-YAML:Simple-Yaml:1.8.4")
    implementation ("org.apache.httpcomponents:httpclient:4.5.13")
}

tasks.withType(JavaCompile::class.java) {
    options.encoding = "UTF-8"
}


tasks {
    shadowJar {
        archiveClassifier.set("")
        manifest {
            attributes["Main-Class"] = "de.ztiger.faibot.FaiBot" // Set your main class name
        }

    }
}


tasks.test {
    useJUnitPlatform()
}