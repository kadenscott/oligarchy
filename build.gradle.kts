plugins {
    id("org.springframework.boot") version "2.5.2"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("java")
}

group = "dev.kscott"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.mattmalec.com/repository/releases/")
}

dependencies {
    compileOnly("org.codehaus.groovy:groovy-all:3.0.8")

//    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.github.docker-java:docker-java-core:3.2.11")
    implementation("com.github.docker-java:docker-java-transport-zerodep:3.2.11")

    implementation("com.mattmalec:Pterodactyl4J:2.BETA_50")
    implementation("io.lettuce:lettuce-core:6.1.4.RELEASE")
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "16"
    targetCompatibility = "16"
}

tasks.withType<Jar> {

}

