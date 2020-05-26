plugins {
    id("maven")
    kotlin("jvm") version "1.3.61"
}

group = "com.github.jaeho"

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("stdlib", "1.2.31"))
    testImplementation("junit:junit:4.12")
    implementation ("org.jetbrains.kotlin:kotlin-reflect:1.3.61")
}