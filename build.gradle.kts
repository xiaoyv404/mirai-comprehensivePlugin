plugins {
    val kotlinVersion = "1.7.20"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.13.0"
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
    implementation("com.google.code.gson:gson:2.10")

    implementation("io.ktor:ktor-server-status-pages:2.1.3")
    implementation("io.ktor:ktor-server-content-negotiation:2.1.3")
    implementation("io.ktor:ktor-server-netty-jvm:2.1.3")
    implementation("io.ktor:ktor-server-auth-jvm:2.1.1")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:2.1.1")
    implementation("io.ktor:ktor-server-websockets-jvm:2.1.3")
    implementation("io.ktor:ktor-server-sessions-jvm:2.1.3")
    implementation("io.ktor:ktor-server-cors:2.1.3")
    implementation("de.svenkubiak:jBCrypt:0.4.3")

    implementation("io.ktor:ktor-client-okhttp-jvm:2.1.3")
    implementation("io.ktor:ktor-serialization-gson:2.1.3")
    compileOnly("net.mamoe.yamlkt:yamlkt-jvm:0.10.2")

    implementation("org.ktorm:ktorm-jackson:3.5.0")
    implementation("org.ktorm:ktorm-support-postgresql:3.5.0")

    implementation("org.postgresql:postgresql:42.5.1")
    implementation("org.ktorm:ktorm-core:3.5.0")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("io.lettuce:lettuce-core:6.2.2.RELEASE")

    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("commons-cli:commons-cli:1.5.0")

    implementation("org.apache.tika:tika-core:2.6.0")
}