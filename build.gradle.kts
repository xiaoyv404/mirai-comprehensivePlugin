plugins {
    val kotlinVersion = "1.5.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.6.7"
}

group = "com.xiaoyv404"
version = "0.3.0"

val ktorm = "3.3.0"
val ktor = "1.5.1"
val yamlKtVersion = "0.7.5"
val httpcomponents = "4.5.4"

repositories {
    mavenLocal()

    // ��������Ч�ֿ⣺https://maven.aliyun.com/mvn/guide
    maven("https://maven.aliyun.com/repository/public")
    maven("https://maven.aliyun.com/repository/google")
    maven("https://maven.aliyun.com/repository/gradle-plugin")
    // ��Ϊ��Դ����https://mirrors.huaweicloud.com
    maven("https://repo.huaweicloud.com/repository/maven")
    // JitPack Զ�ֿ̲⣺https://jitpack.io
    maven("https://jitpack.io")

    // MavenCentral Զ�ֿ̲⣺https://mvnrepository.com
    mavenCentral()
    gradlePluginPortal()
    google()
    jcenter()
}
dependencies {
    compileOnly("net.mamoe.yamlkt:yamlkt-jvm:$yamlKtVersion")

    implementation("mysql:mysql-connector-java:8.0.19")
    implementation("org.ktorm:ktorm-core:$ktorm")
    implementation("com.zaxxer:HikariCP:4.0.2")


    implementation("com.alibaba:fastjson:1.2.76")
    implementation("org.slf4j:slf4j-log4j12:2.0.0-alpha1")

    implementation("io.ktor:ktor-client-cio:$ktor")
    implementation("io.ktor:ktor-server-netty:$ktor")


    implementation("org.apache.httpcomponents:httpclient:$httpcomponents")
    implementation("org.apache.httpcomponents:fluent-hc:$httpcomponents")
    implementation("org.apache.httpcomponents:httpmime:$httpcomponents")

    implementation("redis.clients:jedis:3.6.0")
}