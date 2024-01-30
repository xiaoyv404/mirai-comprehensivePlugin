//for temp, wait for gradle 8.1+
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("jvm") version libs.versions.kotlin
    kotlin("plugin.serialization") version libs.versions.kotlin

    alias(libs.plugins.mirai.console)
}

group = "com.xiaoyv404"
version = "1.0.2"


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
    implementation(libs.gson)

    //webServer
    implementation(libs.ktor.serialization.gson)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.netty.jvm)
    implementation(libs.ktor.server.auth.jvm)
    implementation(libs.ktor.server.auth.jwt.jvm)
    implementation(libs.ktor.server.sessions.jvm)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.client.okhttp.jvm)
    implementation(libs.jbCrypt)

    //config
    compileOnly(libs.yamlkt.jvm)

    //database
    implementation(libs.ktorm.core)
    implementation(libs.ktorm.support.postgresql)
    implementation(libs.postgresql)
    implementation(libs.hikariCP)
    implementation(libs.lettuce.core)

    //commons
    implementation(libs.apache.commons)
    implementation(libs.commons.cli)
    implementation(libs.apache.tika.core)

    testImplementation(libs.testcontainers.testcontainers)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.mirai.core.mork)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.ktor.server.test.host)

    testApi(libs.kotlin.test.junit5)
}
tasks.test {
    useJUnitPlatform()
}