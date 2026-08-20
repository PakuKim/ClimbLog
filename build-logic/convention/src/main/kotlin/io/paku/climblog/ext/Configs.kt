package io.paku.climblog.ext

import org.gradle.api.Project
import java.util.Locale
import java.util.Properties
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
private annotation class ConfigKey(
    val name: String
)

enum class Configs(
    @ConfigKey("PACKAGE_NAME")
    val packageName: String,
    @ConfigKey("KAKAO_NATIVE_APP_KEY")
    val kakaoNativeAppKey: String = "",
) {
    DEV(
        packageName = "io.paku.climblog.dev"
    ),
    PROD(
        packageName = "io.paku.climblog"
    );

    fun toManifestPlaceholders(project: Project): Map<String, Any> {
        return toMap(project)
    }

    fun toBuildConfig(project: Project): Map<String, String> {
        return toMap(project, valueConverter = { "\"$it\"" })
    }

    fun toResValues(project: Project): Map<String, String> {
        return toMap(project, keyConverter = { "key_${it.lowercase(Locale.ENGLISH)}" })
    }

    private inline fun toMap(
        project: Project,
        crossinline keyConverter: (key: String) -> String = { it },
        crossinline valueConverter: (value: String) -> String = { it }
    ): Map<String, String> {
        val localProps = loadLocalProperties(project)

        return Configs::class.declaredMemberProperties.asSequence()
            .mapNotNull { prop ->
                val field = prop.javaField ?: return@mapNotNull null
                val annotation = field.getAnnotation(ConfigKey::class.java) ?: return@mapNotNull null
                val enumValue = prop.get(this) as? String
                val rawValue = if (!enumValue.isNullOrEmpty()) {
                    enumValue
                } else {
                    localProps.getProperty(annotation.name) ?: ""
                }

                keyConverter(annotation.name) to valueConverter(rawValue)
            }
            .toMap()
    }

    private fun loadLocalProperties(project: Project): Properties {
        val properties = Properties()
        val localPropertiesFile = project.rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { properties.load(it) }
        }
        return properties
    }
}