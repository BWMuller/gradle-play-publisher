package de.triplet.gradle.play

import java.io.File
import java.io.FileFilter

internal class ImageFileFilter : FileFilter {
    override fun accept(file: File) = file.extension.toLowerCase() == "png" ||
            file.extension.toLowerCase() == "jpg"
}
