import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import java.util.Base64
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

// Only public client configuration is bundled. The service-role key stays on Supabase.
val localConfiguration = Properties().apply {
    rootProject.file("local.properties").takeIf { it.isFile }?.inputStream()?.use(::load)
}
fun publicConfiguration(name: String) = providers.environmentVariable(name)
    .orElse(localConfiguration.getProperty(name, ""))

val accountDeletionUrl = publicConfiguration("SUPABASE_URL")
val accountDeletionKey = publicConfiguration("SUPABASE_PUBLISHABLE_KEY")
val accountDeletionRetention = publicConfiguration("ACCOUNT_DELETION_RETENTION_NOTICE")
val generateAccountDeletionConfig by tasks.registering {
    val outputDirectory = layout.buildDirectory.dir("generated/accountDeletionResources")
    val configurationValues = mapOf(
        "supabaseUrl" to accountDeletionUrl.get(),
        "publishableKey" to accountDeletionKey.get(),
        "retentionNotice" to accountDeletionRetention.get(),
    )
    inputs.properties(configurationValues)
    outputs.dir(outputDirectory)
    doLast {
        val key = configurationValues.getValue("publishableKey")
        val isAnonymousKey = runCatching {
            val payload = Base64.getUrlDecoder().decode(key.split('.')[1]).decodeToString()
            (JsonSlurper().parseText(payload) as Map<*, *>)["role"] == "anon"
        }.getOrDefault(false)
        require(key.isBlank() || key.matches(Regex("sb_publishable_[A-Za-z0-9_-]+")) || isAnonymousKey) {
            "Only a Supabase publishable or legacy anon key may be bundled in the website."
        }
        outputDirectory.get().file("account-deletion-config.js").asFile.apply {
            parentFile.mkdirs()
            writeText("window.NEVERACHEF_ACCOUNT_CONFIG = ${JsonOutput.toJson(configurationValues)};\n")
        }
    }
}

kotlin {
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
        commonMain {
            resources.srcDir(generateAccountDeletionConfig)
        }
        commonMain.dependencies {
            implementation(projects.shared)

            implementation(libs.compose.ui)
        }
    }
}
