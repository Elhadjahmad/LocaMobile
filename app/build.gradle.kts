plugins {
    id("com.android.application")
}

android {
    namespace = "ca.cuniversel.locamobile"
    compileSdk = 35

    defaultConfig {
        applicationId = "ca.cuniversel.locamobile"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")

    // Évite l'erreur « Duplicate class kotlin... » : toutes les bibliothèques Kotlin sur la même version
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.22"))

    // Exercice 1, étape 1 : Retrofit (les appels HTTP) + Gson (JSON <-> objets Java)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
}
