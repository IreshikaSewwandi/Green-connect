// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}

buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.9.2")
        classpath ("com.google.gms:google-services:4.3.15") // Firebase plugin
    }
}


