plugins {
    id("java")
    id("application")
    antlr
}

group = "pl.edu.pwr.lab"
version = "1.0-SNAPSHOT"

java {
    toolchain{
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    implementation("org.antlr:antlr4-runtime:4.12.0")
    antlr("org.antlr:antlr4:4.5")
}

tasks.generateGrammarSource {
    arguments = arguments + "-visitor"
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

application {
    mainClass.set("pl.edu.pwr.lab.Main")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`;
}