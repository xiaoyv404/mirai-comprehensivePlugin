plugins {
    val kotlinVersion = "1.7.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.12.0"
}

group = "com.xiaoyv404"
version = "1.0.1"


repositories {
    mavenLocal()

    // 阿里云云效仓库：https://maven.aliyun.com/mvn/guide
    maven("https://maven.aliyun.com/repository/public")
    maven("https://maven.aliyun.com/repository/google")
    maven("https://maven.aliyun.com/repository/gradle-plugin")
    // 华为开源镜像：https://mirrors.huaweicloud.com
    maven("https://repo.huaweicloud.com/repository/maven")
    // JitPack 远程仓库：https://jitpack.io
    maven("https://jitpack.io")

    // MavenCentral 远程仓库：https://mvnrepository.com
    mavenCentral()
    gradlePluginPortal()
    google()

    maven("https://plugins.gradle.org/m2")
}
dependencies {
    implementation("io.ktor:ktor-server-status-pages:2.0.2")
    implementation("io.ktor:ktor-server-content-negotiation:2.0.2")
    implementation("io.ktor:ktor-server-netty-jvm:2.0.2")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.0.2")
    implementation("io.ktor:ktor-server-auth-jvm:2.0.2")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:2.0.2")
    implementation("io.ktor:ktor-server-websockets-jvm:2.0.2")
    implementation("io.ktor:ktor-server-sessions-jvm:2.0.2")
    implementation("io.ktor:ktor-server-cors:2.0.3")

    implementation("io.ktor:ktor-client-okhttp-jvm:2.0.2")
    implementation("io.ktor:ktor-server-content-negotiation:2.0.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.0.2")
    compileOnly("net.mamoe.yamlkt:yamlkt-jvm:0.10.2")

    implementation("org.ktorm:ktorm-jackson:3.5.0")
    implementation("org.ktorm:ktorm-support-postgresql:3.5.0")

    implementation("ch.qos.logback:logback-classic:1.2.11")

    implementation("org.postgresql:postgresql:42.3.6")
    implementation("org.ktorm:ktorm-core:3.5.0")
    implementation("com.zaxxer:HikariCP:5.0.1")

    implementation("io.lettuce:lettuce-core:6.1.8.RELEASE")

    implementation("de.svenkubiak:jBCrypt:0.4.3")


    implementation("org.apache.httpcomponents:httpclient:4.5.13")
    implementation("org.apache.httpcomponents:fluent-hc:4.5.13")
    implementation("org.apache.httpcomponents:httpmime:4.5.13")


    implementation("org.apache.tika:tika-core:2.3.0")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("commons-cli:commons-cli:1.5.0")

    implementation("com.google.code.gson:gson:2.9.0")
}