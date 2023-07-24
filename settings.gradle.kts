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

plugins {
    id("com.gradle.enterprise") version("3.13.4")
}

gradleEnterprise {
    if (System.getenv("CI") != null) {
        buildScan {
            publishAlways()
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
        }
    }
}


rootProject.name = "ComprehensivePlugin"
