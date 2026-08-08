plugins {
    application
}

val cregisSdkVersion = providers.gradleProperty("cregisSdkVersion").orElse("1.0.0-rc.1")

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("com.cregis:cregis-sdk-java:${cregisSdkVersion.get()}")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

application {
    mainClass = "com.cregis.smoke.ConsumerSmoke"
}
