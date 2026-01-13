import com.vanniktech.maven.publish.SonatypeHost

plugins {
    java
    `java-library`
    alias(libs.plugins.maven.publish)
}

apply(from = "version.gradle.kts")


java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.bundles.test)
}

tasks.test {
    useJUnitPlatform()
}

// Create a lazy version provider that only executes during publishing
val versionProvider = provider {
    if (gradle.startParameter.taskNames.any { it.contains("publish") }) {
        val getVersionFunc = extra["getVersionFromGitTag"] as () -> String
        getVersionFunc()
    } else {
        "3.0.0" // fallback for non-publishing tasks
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
    signAllPublications()

    coordinates("nl.bartlouwers", "earcut4j", versionProvider.get())

    pom {
        name = "earcut4j"
        description = "A Java port of the earcut polygon triangulation library, based on the JavaScript version from @mapbox/earcut"
        inceptionYear = "2020"
        url = "https://github.com/louwers/earcut4j/"
        licenses {
            license {
                name = "ISC License"
                url = "https://github.com/louwers/earcut4j/blob/main/LICENSE"
                distribution = "https://github.com/louwers/earcut4j/blob/main/LICENSE"
            }
        }
        developers {
            developer {
                id = "louwers"
                name = "Bart Louwers"
                url = "https://github.com/louwers/"
            }
        }
        scm {
            url = "https://github.com/louwers/earcut4j/"
            connection = "scm:git:git://github.com/louwers/earcut4j.git"
            developerConnection = "scm:git:ssh://git@github.com/louwers/earcut4j.git"
        }
    }
}
