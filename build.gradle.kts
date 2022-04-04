plugins {
    val kotlinVersion = "1.6.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.11.0-M1"
}

group = "com.xiaoyv404"
version = "1.0.1"

val ktorm = "3.4.1"
val ktorVersion = "1.6.8"
val yamlKtVersion = "0.10.2"
val httpcomponents = "4.5.13"

repositories {
    mavenLocal()

    // °¢ÀïÔÆÔÆÐ§²Ö¿â£ºhttps://maven.aliyun.com/mvn/guide
    maven("https://maven.aliyun.com/repository/public")
    maven("https://maven.aliyun.com/repository/google")
    maven("https://maven.aliyun.com/repository/gradle-plugin")
    // »ªÎª¿ªÔ´¾µÏñ£ºhttps://mirrors.huaweicloud.com
    maven("https://repo.huaweicloud.com/repository/maven")
    // JitPack Ô¶³Ì²Ö¿â£ºhttps://jitpack.io
    maven("https://jitpack.io")

    // MavenCentral Ô¶³Ì²Ö¿â£ºhttps://mvnrepository.com
    mavenCentral()
    gradlePluginPortal()
    google()

    maven("https://plugins.gradle.org/m2")
}
dependencies {
    compileOnly("net.mamoe.yamlkt:yamlkt-jvm:$yamlKtVersion")

    implementation("org.ktorm:ktorm-jackson:$ktorm")
    implementation("org.ktorm:ktorm-support-postgresql:3.4.1")

    implementation("ch.qos.logback:logback-classic:1.2.11")

    implementation("org.postgresql:postgresql:42.3.3")
    implementation("org.ktorm:ktorm-core:$ktorm")
    implementation("com.zaxxer:HikariCP:5.0.1")


    implementation("com.alibaba:fastjson:1.2.80")

    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-websockets:$ktorVersion")
    implementation("io.ktor:ktor-server-sessions:$ktorVersion")
    implementation("de.svenkubiak:jBCrypt:0.4.3")


    implementation("org.apache.httpcomponents:httpclient:$httpcomponents")
    implementation("org.apache.httpcomponents:fluent-hc:$httpcomponents")
    implementation("org.apache.httpcomponents:httpmime:$httpcomponents")


    implementation("org.apache.tika:tika-core:2.3.0")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("commons-cli:commons-cli:1.5.0")

    implementation("org.reflections:reflections:0.10.2")
    implementation("com.google.guava:guava:31.1-jre")
    implementation("com.google.code.gson:gson:2.9.0")
}