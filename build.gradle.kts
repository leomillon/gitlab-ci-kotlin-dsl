import com.adarshr.gradle.testlogger.theme.ThemeType

plugins {
    kotlin("jvm") version "1.5.30"
    kotlin("plugin.serialization") version "1.5.30"
    `maven-publish`
    signing
    id("org.jetbrains.dokka") version "1.5.0"
    id("com.adarshr.test-logger") version "3.0.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.2.1")
    implementation("com.charleskorn.kaml:kaml:0.35.3")

    testImplementation(platform("org.junit:junit-bom:5.7.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.20.2")
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + listOf("-Xjsr305=strict")
            jvmTarget = JavaVersion.VERSION_11.toString()
        }
    }

    withType<Test>().configureEach {
        useJUnitPlatform()
        jvmArgs(
            "-Duser.language=en"
        )
    }
}

val dokkaJar by tasks.registering(Jar::class) {
    dependsOn("dokkaHtml")
    archiveClassifier.set("javadoc")
    from(buildDir.resolve("dokka"))
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifact(dokkaJar)

            pom {
                name.set("GitlabCi Kotlin DSL")
                description.set("Library providing Kotlin DSL to configure GitlabCI file")
                url.set("https://github.com/pcimcioch/gitlab-ci-kotlin-dsl")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/pcimcioch/gitlab-ci-kotlin-dsl.git")
                    developerConnection.set("scm:git:git@github.com:pcimcioch/gitlab-ci-kotlin-dsl.git")
                    url.set("https://github.com/pcimcioch/gitlab-ci-kotlin-dsl")
                }
                developers {
                    developer {
                        id.set("pcimcioch")
                        name.set("Przemysław Cimcioch")
                        email.set("cimcioch.przemyslaw@gmail.com")
                    }
                }
            }
        }
    }

    repositories {
        val url = if (project.version.toString()
                .contains("SNAPSHOT")
        ) "https://oss.sonatype.org/content/repositories/snapshots" else "https://oss.sonatype.org/service/local/staging/deploy/maven2"
        maven(url) {
            credentials {
                username = project.findProperty("ossrh.username")?.toString() ?: ""
                password = project.findProperty("ossrh.password")?.toString() ?: ""
            }
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}

testlogger {
    theme = ThemeType.MOCHA
}
