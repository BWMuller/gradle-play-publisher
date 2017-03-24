package de.triplet.gradle.play

/*
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class PlayPublisherPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val log = project.logger

        if (!project.plugins.any { p -> p is AppPlugin }) {
            throw IllegalStateException("The 'com.android.application' plugin is required.")
        }

        val android = project.extensions.getByType(AppExtension::class.java)
        val extension = project.extensions.create("play", PlayPublisherPluginExtension::class.java)

        android.extensions.playAccountConfigs = project.container(PlayAccountConfig::class.java)

        android.defaultConfig.ext.playAccountConfig = null

        android.productFlavors.whenObjectAdded { flavor ->
            flavor.ext.playAccountConfig = android.defaultConfig.ext.playAccountConfig)
        }

        android.applicationVariants.whenObjectAdded { variant ->
            if (variant.buildType.isDebuggable()) {
                log.debug("Skipping debuggable build type ${variant.buildType.name}.")
                return@whenObjectAdded
            }

            val bootstrapTaskName = "bootstrap${variant.name.capitalize()}PlayResources"
            val playResourcesTaskName = "generate${variant.name.capitalize()}PlayResources"
            val publishApkTaskName = "publishApk${variant.name.capitalize()}"
            val publishListingTaskName = "publishListing${variant.name.capitalize()}"
            val publishTaskName = "publish${variant.name.capitalize()}"

            // Create and configure bootstrap task for this variant.
            val bootstrapTask = project.tasks.create(bootstrapTaskName, BootstrapTask::class.java)
            bootstrapTask.extension = extension
            bootstrapTask.variant = variant
            val accountConfigForFlavor = variant.productFlavors.firstOrNull { it.playAccountConfig != null }?.playAccountConfig
            val defaultAccountConfig = android.defaultConfig.ext.playAccountConfig
            if (!variant.flavorName.isEmpty()) {
                bootstrapTask.outputFolder = project.file("src/${variant.flavorName}/play")
            } else {
                bootstrapTask.outputFolder = project.file("src/main/play")
            }
            bootstrapTask.playAccountConfig = accountConfigForFlavor ?: defaultAccountConfig
            bootstrapTask.description = "Downloads the play store listing for the ${variant.name.capitalize()} build. No download of image resources. See #18."
            bootstrapTask.group = PLAY_STORE_GROUP

            // Create and configure task to collect the play store resources.
            val playResourcesTask = project.tasks.create(playResourcesTaskName, GeneratePlayResourcesTask::class.java)

            playResourcesTask.inputs.file(project.file("src/main/play"))
            if (!variant.flavorName.isEmpty()) {
                playResourcesTask.inputs.file(project.file("src/${variant.flavorName}/play"))
            }
            playResourcesTask.inputs.file(project.file("src/${variant.buildType.name}/play"))
            playResourcesTask.inputs.file(project.file("src/${variant.name}/play"))

            playResourcesTask.outputFolder = project.file("build/outputs/play/${variant.name}")
            playResourcesTask.description = "Collects play store resources for the ${variant.name.capitalize()} build"
            playResourcesTask.group = PLAY_STORE_GROUP

            // Create and configure publisher meta task for this variant
            val publishListingTask = project.tasks.create(publishListingTaskName, PlayPublishListingTask::class.java)
            publishListingTask.extension = extension
            publishListingTask.playAccountConfig = accountConfigForFlavor ?: defaultAccountConfig
            publishListingTask.variant = variant
            publishListingTask.inputFolder = playResourcesTask.outputFolder
            publishListingTask.description = "Updates the play store listing for the ${variant.name.capitalize()} build"
            publishListingTask.group = PLAY_STORE_GROUP

            // Attach tasks to task graph.
            publishListingTask.dependsOn(playResourcesTask)

            if (variant.isSigningReady()) {
                // Create and configure publisher apk task for this variant.
                val publishApkTask = project.tasks.create(publishApkTaskName, PlayPublishApkTask::class.java)
                publishApkTask.extension = extension
                publishApkTask.playAccountConfig = accountConfigForFlavor ?: defaultAccountConfig
                publishApkTask.variant = variant
                publishApkTask.inputFolder = playResourcesTask.outputFolder
                publishApkTask.description = "Uploads the APK for the ${variant.name.capitalize()} build"
                publishApkTask.group = PLAY_STORE_GROUP

                val publishTask = project.tasks.create(publishTaskName)
                publishTask.description = "Updates APK and play store listing for the ${variant.name.capitalize()} build"
                publishTask.group = PLAY_STORE_GROUP

                // Attach tasks to task graph.
                publishTask.dependsOn(publishApkTask)
                publishTask.dependsOn(publishListingTask)
                publishApkTask.dependsOn(playResourcesTask)
                publishApkTask.dependsOn(variant.assemble)
            } else {
                log.warn("Signing not ready. Did you specify a signingConfig for the variation ${variant.name.capitalize()}?")
            }
        }
    }
}
*/
