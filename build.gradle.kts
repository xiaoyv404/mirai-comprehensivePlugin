plugins {
    kotlin("jvm") version "1.4.30-M1"
    kotlin("plugin.serialization") version "1.4.0"
    id("net.mamoe.mirai-console") version "2.5.0"
}

group = "com.xiaoyv404"
version = "0.3.0"

val ktorm = "3.3.0"
val ktorVersion = "1.5.1"
val yamlKtVersion = "0.7.5"
val httpcomponents = "4.5.4"

repositories {
    //¹úÄÚ¾µÏñÔ´
    maven { url = uri("https://mirrors.huaweicloud.com/repository/maven") }
    maven { url = uri("https://maven.aliyun.com/nexus/content/repositories/jcenter") }
    maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap") }
    mavenLocal()
    mavenCentral()
    jcenter()
}
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.30-M1")

    implementation("mysql:mysql-connector-java:8.0.19")
    implementation("org.ktorm:ktorm-core:$ktorm")
    implementation("com.zaxxer:HikariCP:4.0.2")


    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
    implementation("org.slf4j:slf4j-log4j12:2.0.0-alpha1")
    implementation("net.mamoe.yamlkt:yamlkt-jvm:$yamlKtVersion")


    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    implementation("org.apache.httpcomponents:httpclient:$httpcomponents")
    implementation("org.apache.httpcomponents:fluent-hc:$httpcomponents")
    implementation("org.apache.httpcomponents:httpmime:$httpcomponents")
}