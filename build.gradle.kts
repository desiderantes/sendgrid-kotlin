import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.kotlin.dsl.*

/**
 * Commands:
 *   - gradle build
 *   - gradle test
 *   - gradle assemble
 *   - gradle uploadArchives
 *
 * To execute the "uploadArchives" task, the following properties must be specified
 * in an external "gradle.properties" file:
 *   - sonatypeUsername
 *   - sonatypePassword
 */

buildscript {
    dependencies {
    }
    repositories {
        mavenCentral()
    }
}

plugins {
    java
    `java-library-distribution`
    id("com.github.johnrengelman.shadow") version "2.0.4"
    maven
    `maven-publish`
    signing
    `build-scan`
    kotlin("jvm") version "1.2.61"
}

group = "com.sendgrid"
version = "4.2.1"
extra["packaging"] = "jar"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}




if (!hasProperty("sonatypeUsername")) {
    extra["sonatypeUsername"] = null
    extra["sonatypePassword"] = null
}

tasks.withType<Wrapper> {
    gradleVersion = "4.9"
}



dependencies {
    implementation("com.sendgrid:java-http-client:4.1.0")
    implementation("com.fasterxml.jackson.core:jackson-core:2.9.6")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.9.6")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.6")
    testImplementation(group = "junit", name = "junit", version = "4.12")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

allprojects {
    gradle.projectsEvaluated {
        tasks.withType<JavaCompile> {
            options.compilerArgs = options.compilerArgs.plus("-Xlint:unchecked").plus("-Xlint:deprecation")
        }
    }
}


// adds "with-dependencies" to the shadowJar name
val shadowJar = tasks["shadowJar"] as ShadowJar
shadowJar.apply {
    classifier = "jar"
    baseName = "sendgrid"
    manifest {
        attributes(
                mapOf(
                        "Implementation-Title" to "SendGrid",
                        "Implementation-Version" to version

                )
        )
    }
}


val renameSendGridVersionJarToSendGridJar = tasks.create("renameSendGridVersionJarToSendGridJar") {
    doLast {
        file("$projectDir/repo/com/sendgrid/${shadowJar.archiveName}").renameTo(file("$projectDir/repo/com/sendgrid/sendgrid-java-latest.jar"))

        copy {
            from("$buildDir/libs/${shadowJar.archiveName}")
            into("$projectDir/repo/com/sendgrid")
        }
        file("$projectDir/repo/com/sendgrid/${shadowJar.archiveName}").renameTo(file("$projectDir/repo/com/sendgrid/sendgrid-java-${version}.jar"))
    }
}

tasks {
    "build" {
        this.doLast {
            copy {
                println("Copying ${shadowJar.archiveName} to $projectDir/repo/com/sendgrid")
                from("$buildDir/libs/${shadowJar.archiveName}")
                into("$projectDir/repo/com/sendgrid")
            }
            renameSendGridVersionJarToSendGridJar.setMustRunAfter(listOf(this))
        }
    }
}

buildScan {
    setLicenseAgreementUrl("https://gradle.com/terms-of-service")
    setLicenseAgree("yes")

    publishAlways()
}

val startPrism = tasks.create("startPrism", Exec::class.java) {
    workingDir = file("scripts")
    commandLine = listOf("./startPrism.sh")
}

val javadocJar = tasks.create("javadocJar", Jar::class.java) {
    classifier = "javadoc"
    dependsOn(tasks.getting(Javadoc::class))
    from("build/docs/javadoc")
}

val sourcesJar = tasks.create("sourcesJar", Jar::class.java) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles sources JAR"
    classifier = "sources"
    from(java.sourceSets["main"].allSource)
}

signing {
    setRequired { gradle.taskGraph.hasTask("uploadArchives") }
    sign(configurations.archives)
}



publishing {
    publications {
        create("default", MavenPublication::class.java) {
            from(components["java"])
            artifact(shadowJar)
            artifact(javadocJar)
            artifact(sourcesJar)
        }
    }
    repositories {
        maven {
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
        }
    }
}


/*
uploadArchiveTask{} uploadArchives {
    repositories {
        mavenDeployer {
            beforeDeployment { MavenDeployment deployment -> signing.signPom(deployment) }
            repository(url: ) {
            authentication(userName: sonatypeUsername, password: sonatypePassword)
        }

            pom.project {
                name "sendgrid-java"
                packaging "jar"
                description "SendGrid Java helper library"
                url "https://github.com/sendgrid/sendgrid-java"

                scm {
                    url "scm:git@github.com:sendgrid/sendgrid-java.git"
                    connection "scm:git@github.com:sendgrid/sendgrid-java.git"
                    developerConnection "scm:git@github.com:sendgrid/sendgrid-java.git"
                }

                licenses {
                    license {
                        name "MIT License"
                        url "http://opensource.org/licenses/MIT"
                        distribution "repo"
                    }
                }

                developers {
                    developer {
                        id "thinkingserious"
                        name "Elmer Thomas"
                    }
                    developer {
                        id "scottmotte"
                        name "Scott Motte"
                    }
                    developer {
                        id "elbou8"
                        name "Yamil Asusta"
                    }
                    developer {
                        id "eddiezane"
                        name "Eddie Zaneski"
                    }
                }
            }
        }
    }
}

artifacts {
    archives shadowJar
            archives
            archives
            archives
}

compileKotlin {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
compileTestKotlin {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}*/
