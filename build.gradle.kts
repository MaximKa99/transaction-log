plugins {
    java
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.epam"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("io.debezium:debezium-api:3.1.1.Final")
    implementation("io.debezium:debezium-connector-postgres:3.1.1.Final")
    implementation("io.debezium:debezium-embedded:3.1.1.Final")
    implementation("io.debezium:debezium-core:3.1.1.Final")
    implementation("org.apache.kafka:connect-api:3.7.0")
    implementation("org.apache.kafka:connect-runtime:3.7.0")
    implementation("net.objecthunter:exp4j:0.4.8")
    implementation("org.postgresql:postgresql:42.7.5")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}