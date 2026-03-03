import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.*

plugins {
	alias(libs.plugins.kotlinMultiplatform)
	alias(libs.plugins.kotlinxSerialization)
	alias(libs.plugins.sqldelight)
	alias(libs.plugins.androidApplication)
	alias(libs.plugins.composeMultiplatform)
	alias(libs.plugins.composeCompiler)
	alias(libs.plugins.composeHotReload)
	alias(libs.plugins.buildkonfig)
}

kotlin {
	androidTarget {
		compilerOptions {
			jvmTarget.set(JvmTarget.JVM_11)
		}
	}

	listOf(
		iosArm64(),
		iosSimulatorArm64()
	).forEach { iosTarget ->
		iosTarget.binaries.framework {
			baseName = "ComposeApp"
			isStatic = true
		}
	}

	jvm()

	js {
		browser()
		binaries.executable()
	}

	@OptIn(ExperimentalWasmDsl::class)
	wasmJs {
		browser()
		binaries.executable()
	}

	sourceSets {
		all {
			languageSettings.optIn("kotlin.time.ExperimentalTime")
		}
		androidMain.dependencies {
			implementation(libs.ui.tooling.preview)
			implementation(libs.androidx.activity.compose)
			implementation(libs.ktor.client.okhttp)
			implementation(libs.ktor.client.android)
			implementation(libs.android.driver)
		}
		iosMain.dependencies {
			implementation(libs.ktor.client.darwin)
			implementation(libs.native.driver)
		}
		commonMain.dependencies {
//			Compose
			implementation(libs.jetbrains.runtime)
			implementation(libs.foundation)
			implementation(libs.material3)
			implementation(libs.material.icons.extended)
			implementation(libs.ui)
			implementation(libs.components.resources)
			implementation(libs.ui.tooling.preview)

//			AndroidX
			implementation(libs.androidx.lifecycle.viewmodelCompose)
			implementation(libs.androidx.lifecycle.runtimeCompose)
			implementation(libs.androidx.navigation.compose)

//			Image Loading
			implementation(libs.kamel.image.default)

//			Supabase
			implementation(project.dependencies.platform("io.github.jan-tennert.supabase:bom:3.4.0"))
			implementation(libs.supabase.postgrest.kt)
			implementation(libs.supabase.auth.kt)
			implementation(libs.supabase.realtime.kt)
			implementation(libs.supabase.storage.kt)

//			SQLDelight
			implementation(libs.kotlinx.coroutines.core)
			implementation(libs.kotlinx.datetime)
			implementation(libs.koin.core)
			implementation(libs.ktor.client.core)
			implementation(libs.ktor.client.content.negotiation)
			implementation(libs.ktor.serialization.kotlinx.json)
			implementation(libs.runtime)

//			QR Code
			implementation(libs.qrose)
		}
		commonTest.dependencies {
			implementation(libs.kotlin.test)
		}
		jvmMain.dependencies {
			implementation(compose.desktop.currentOs)
			implementation(libs.kotlinx.coroutinesSwing)
			implementation(libs.ktor.client.cio)
		}
		jsMain.dependencies {
			implementation(libs.ktor.client.js)
		}
	}
}

android {
	namespace = "containerised.pos"
	compileSdk = libs.versions.android.compileSdk.get().toInt()
	packaging.resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"

	defaultConfig {
		applicationId = "containerised.pos"
		minSdk = libs.versions.android.minSdk.get().toInt()
		targetSdk = libs.versions.android.targetSdk.get().toInt()
		versionCode = 1
		versionName = "1.0"
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
}

// Load local.properties
val localProperties = Properties().apply {
	val localPropertiesFile = rootProject.file("local.properties")
	if (localPropertiesFile.exists()) {
		localPropertiesFile.inputStream().use { load(it) }
	}
}

buildkonfig {
	packageName = "containerised.pos"
	defaultConfigs {
		buildConfigField(
			STRING, "SUPABASE_URL",
			System.getenv("SUPABASE_URL") ?: localProperties.getProperty("supabase.url", "")
		)
		buildConfigField(
			STRING, "SUPABASE_KEY",
			System.getenv("SUPABASE_KEY") ?: localProperties.getProperty("supabase.key", "")
		)
	}
}

dependencies {
	debugImplementation(libs.ui.tooling)
}

compose.desktop {
	application {
		mainClass = "containerised.pos.MainKt"

		nativeDistributions {
			targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)

//			Package metadata
			packageName = "containerised.pos"
			packageVersion = "1.0.0"
			description = "A point of sale application for F&B SMEs."
			copyright = "2025-2026 Containerised"
			vendor = "Containerised"

//			Package icons
			windows.iconFile = project.file("icons/icon.ico")
			macOS.iconFile = project.file("icons/icon.icns")
			linux.iconFile = project.file("icons/icon.png")

			windows {
				menu = true
				menuGroup = "Containerised"
			}
		}
	}
}

sqldelight {
	databases {
		create("AppDatabase") {
			packageName.set("containerised.pos.database")
		}
	}
}
