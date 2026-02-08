import com.github.spotbugs.snom.SpotBugsTask

plugins {
    java
    checkstyle
    pmd
    id("com.diffplug.spotless") version "8.2.1"
    id("com.github.spotbugs") version "6.4.8"
    id("org.springframework.boot") version "4.0.2"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "ca.mcgill.ecse321"
version = "0.0.1-SNAPSHOT"
description = "Fashion store backend"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.14.0")
}

checkstyle {
    toolVersion = "13.2.0"
    isIgnoreFailures = false
}

spotless {
    format("misc") {
        target("*.gradle", ".gitattributes", ".gitignore", "*.kts")
        trimTrailingWhitespace()
        leadingTabsToSpaces(4)
        endWithNewline()
    }
    java {
        googleJavaFormat("1.34.1").aosp()
        trimTrailingWhitespace()
        formatAnnotations()
        removeUnusedImports()
        endWithNewline()
    }
}

pmd {
    toolVersion = "7.21.0"
    isConsoleOutput = true
    rulesMinimumPriority = 5
    ruleSetFiles = files(rootProject.file("config/pmd/ruleset.xml"))
    ruleSets = listOf()
}

spotbugs {
    ignoreFailures = false
    showStackTraces = true
    showProgress = true
    effort = com.github.spotbugs.snom.Effort.MAX
    reportLevel = com.github.spotbugs.snom.Confidence.LOW
    excludeFilter.set(file("config/spotbugs/exclude.xml"))
}

tasks.checkstyleMain {
    exclude("**/model/**")
}

tasks.pmdMain {
    exclude("**/model/**")
}

tasks.withType<SpotBugsTask>().configureEach {
    reports {
        create("text") {
            required.set(true)
        }
        create("html") {
            required.set(true)
            setStylesheet("fancy-hist.xsl")
        }
    }
}

tasks.named("check") {
    dependsOn("spotlessApply")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
