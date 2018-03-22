package de.triplet.gradle.play.validation

import org.gradle.api.Project

interface IValidator<T> {

    /**
     * @return true if asset is valid, otherwise false
     */
    boolean validate(Project project, T asset)

}