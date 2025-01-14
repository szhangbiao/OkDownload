
buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
        // google() 国内阿里云替代
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        // mavenCentral() 国内阿里云替代
        maven { url = uri("https://maven.aliyun.com/repository/central") }
        // gradlePluginPortal() 国内阿里云替代
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
    }

    dependencies {

    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}

subprojects {
    apply("${project.rootDir}/gradle/maven-publish.gradle")
}

allprojects {
    repositories {
        // 本地 maven配置
        maven { url = uri("${project.rootDir}/.mavenLocal/repository") }
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        // google() 国内阿里云替代
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        // mavenCentral() 国内阿里云替代
        maven { url = uri("https://maven.aliyun.com/repository/central") }
        // okdownload Snapshots version
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        //-------------- 阿里云配置 -----------------
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven {
            credentials {
                username = Config.MAVEN.credentials.username
                password = Config.MAVEN.credentials.password
            }
            url = Config.MAVEN.snapshotUrl
        }
        maven {
            credentials {
                username = Config.MAVEN.credentials.username
                password = Config.MAVEN.credentials.password
            }
            url = Config.MAVEN.releaseUrl
        }
        //-----------------------------------------
    }
}