import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id("kotlin-kapt")
}
val secretsPropertiesFiel = rootProject.file("secrets.properties")
val secretsProperties =  Properties()
secretsProperties.load( FileInputStream(secretsPropertiesFiel))

android {
    namespace = "com.example.myapplication"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "MONGO_URI", "\"${secretsProperties["MONGO_URI"].toString()}\"")
    }

    buildTypes {
        debug {
            buildConfigField("String", "MONGO_URI", "\"mongodb+srv://root123:1212003@quizapp.lx5nz.mongodb.net/?retryWrites=true&w=majority&appName=quizapp\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "MONGO_URI", "\"mongodb+srv://root123:1212003@quizapp.lx5nz.mongodb.net/?retryWrites=true&w=majority&appName=quizapp\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    viewBinding {
        enable  = true
    }
    buildFeatures{
        dataBinding = true
        viewBinding = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes.add("META-INF/INDEX.LIST")
            excludes.add("META-INF/DEPENDENCIES")
            excludes.add("META-INF/LICENSE")
            excludes.add("META-INF/LICENSE.txt")
            excludes.add("META-INF/license.txt")
            excludes.add("META-INF/NOTICE")
            excludes.add("META-INF/NOTICE.txt")
            excludes.add("META-INF/notice.txt")
            excludes.add("META-INF/ASL2.0")
            excludes.add("META-INF/*.kotlin_module")
            excludes.add("META-INF/io.netty.versions.properties")
            excludes.add("META-INF/native-image/org.mongodb/bson/native-image.properties")
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.core.splashscreen)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation("com.google.android.gms:play-services-auth:21.3.0")

    implementation("io.ktor:ktor-server-core:2.3.1")
    implementation("io.ktor:ktor-server-netty:2.3.1")

    implementation(libs.javax.persistence.api)
    implementation(libs.hibernate.core.v557final)
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.4")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    implementation(libs.androidx.lifecycle.viewmodel.ktx.v231)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation (libs.androidx.appcompat.v170)
    implementation ("com.google.android.material:material:1.6.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:4.10.1")
    implementation("org.litote.kmongo:kmongo-coroutine:4.5.1")
    implementation("io.projectreactor:reactor-core:3.4.0")
    implementation("org.slf4j:slf4j-api:1.6.1")
    implementation("org.slf4j:slf4j-simple:1.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.6.4")


    implementation("org.litote.kmongo:kmongo:4.9.0")
    implementation("org.litote.kmongo:kmongo-coroutine:4.9.0")

}

