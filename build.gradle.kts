plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "de.ztiger"
version = "1.3"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.github.twitch4j:twitch4j:1.15.0")
    implementation("net.dv8tion:JDA:5.0.0-beta.11") {
        exclude(module="opus-java")
    }
    implementation ("ch.qos.logback:logback-classic:1.4.6")
    implementation ("org.slf4j:slf4j-api:2.0.4")
    implementation ("mysql:mysql-connector-java:8.0.32")
    implementation ("io.github.cdimascio:dotenv-java:2.3.2")
    implementation ("com.vaadin.external.google:android-json:0.0.20131108.vaadin1")
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