package de.triplet.gradle.play

import com.android.build.gradle.api.ApkVariantOutput
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.FileContent
import com.google.api.services.androidpublisher.model.Apk
import com.google.api.services.androidpublisher.model.ApkListing
import com.google.api.services.androidpublisher.model.Track
import org.gradle.api.tasks.TaskAction
import java.io.File

open class PlayPublishApkTask : PlayPublishTask() {
    var inputFolder: File? = null

    @TaskAction
    fun publishApks() {
        publish()

        val versionCodes = variant.outputs
                .filter { it is ApkVariantOutput }
                .map { publishApk(FileContent(MIME_TYPE_APK, it.outputFile)) }
                .map { it.versionCode }

        val track = Track().setVersionCodes(versionCodes)
        if (extension.track == "rollout") {
            track.userFraction = extension.userFraction
        }
        edits.tracks()
                .update(variant.applicationId, editId, extension.track, track)
                .execute()

        edits.commit(variant.applicationId, editId)
                .execute()
    }

    fun publishApk(apkFile: FileContent): Apk {
        val apk = edits.apks()
                .upload(variant.applicationId, editId, apkFile)
                .execute()

        if (extension.untrackOld && extension.track != "alpha") {
            val untrackChannels = if (extension.track == "beta") arrayOf("alpha") else arrayOf("alpha", "beta")
            untrackChannels.forEach { channel ->
                try {
                    val track = edits.tracks()
                            .get(variant.applicationId, editId, channel)
                            .execute()
                    track.versionCodes = track.versionCodes.filter { it > apk.versionCode }

                    edits.tracks()
                            .update(variant.applicationId, editId, channel, track)
                            .execute()
                } catch (e: GoogleJsonResponseException) {
                    // Just skip if there is no version in track
                    if (e.details.code != 404) {
                        throw e
                    }
                }
            }
        }

        // Upload Proguard mapping.txt if available
        if (variant.mappingFile?.exists() ?: false) {
            val fileStream = FileContent("application/octet-stream", variant.mappingFile)
            edits.deobfuscationfiles()
                    .upload(variant.applicationId, editId, apk.versionCode, "proguard", fileStream)
                    .execute()
        }

        inputFolder?.apply {
            if (exists()) {
                // Matches if locale have the correct naming e.g. en-US for play store
                listFiles(LocaleFileFilter()).forEach { updateWhatsNew(apk, it) }
            }
        }

        return apk
    }

    private fun updateWhatsNew(apk: Apk, locale: File) {
        var whatsNewFile = File(locale, "$FILE_NAME_FOR_WHATS_NEW_TEXT-${extension.track}")

        if (!whatsNewFile.exists()) {
            whatsNewFile = File(locale, FILE_NAME_FOR_WHATS_NEW_TEXT)
        }

        if (whatsNewFile.exists()) {
            val whatsNewText = whatsNewFile.readAndTrim(project, MAX_CHARACTER_LENGTH_FOR_WHATS_NEW_TEXT, extension.errorOnSizeLimit)

            val newApkListing = ApkListing().setRecentChanges(whatsNewText)
            edits.apklistings()
                    .update(variant.applicationId, editId, apk.versionCode, locale.name, newApkListing)
                    .execute()
        }
    }
}
