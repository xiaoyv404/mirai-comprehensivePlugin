pluginManagement {
    repositories {
        mavenLocal()
        maven( "https://maven.aliyun.com/repository/releases")
        maven( "https://maven.aliyun.com/repository/public")
        mavenCentral()
//        maven( "https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://plugins.gradle.org/m2")
        gradlePluginPortal()
    }
}


rootProject.name = "ComprehensivePlugin"
