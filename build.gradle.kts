plugins {
	// this is necessary to avoid the plugins to be loaded multiple times
	// in each subproject's classloader
	alias(libs.plugins.androidApplication) apply false
	alias(libs.plugins.androidLibrary) apply false
	alias(libs.plugins.composeHotReload) apply false
	alias(libs.plugins.composeMultiplatform) apply false
	alias(libs.plugins.composeCompiler) apply false
	alias(libs.plugins.kotlinMultiplatform) apply false
	id("jacoco")
	id("org.sonarqube") version "7.2.3.7755"
}

sonar {
	properties {
		property("sonar.projectKey", "COS40006")
		property("sonar.projectName", "COS40006 Containerised POS")
		property("sonar.organization", "containerised")
		property("sonar.host.url", "http://localhost:9000")
		property("sonar.coverage.jacoco.xmlReportPaths", "${layout.projectDirectory.file("composeApp/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml").asFile.absolutePath}")
	}
}

tasks.named("sonar") {
	dependsOn(":composeApp:jacocoTestReport")
}
