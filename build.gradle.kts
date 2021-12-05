plugins {
    val kotlinVersion = "1.5.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.8.2"
}

group = "com.xiaoyv404"
version = "1.0.0-beta"

val ktorm = "3.3.0"
val ktor = "1.5.1"
val yamlKtVersion = "0.10.2"
val httpcomponents = "4.5.4"

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
}
dependencies {
    compileOnly("net.mamoe.yamlkt:yamlkt-jvm:$yamlKtVersion")
    compileOnly("org.ktorm:ktorm-jackson:$ktorm")
    compileOnly("org.ktorm:ktorm-support-mysql:$ktorm")


    implementation("mysql:mysql-connector-java:8.0.19")
    implementation("org.ktorm:ktorm-core:$ktorm")
    implementation("com.zaxxer:HikariCP:4.0.2")


    implementation("com.alibaba:fastjson:1.2.76")
    implementation("org.slf4j:slf4j-simple:1.7.25")

    implementation("io.ktor:ktor-client-cio:$ktor")
    implementation("io.ktor:ktor-server-netty:$ktor")
    implementation("io.ktor:ktor-jackson:$ktor")


    implementation("org.apache.httpcomponents:httpclient:$httpcomponents")
    implementation("org.apache.httpcomponents:fluent-hc:$httpcomponents")
    implementation("org.apache.httpcomponents:httpmime:$httpcomponents")
    implementation("org.apache.commons:commons-lang3:3.12.0")


    implementation("redis.clients:jedis:3.6.0")

    implementation("org.apache.tika:tika-core:2.1.0")

}