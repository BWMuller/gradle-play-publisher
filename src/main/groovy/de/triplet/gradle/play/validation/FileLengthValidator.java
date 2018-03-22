package de.triplet.gradle.play.validation;

import org.gradle.api.Project;

import java.io.File;

import de.triplet.gradle.play.LimitExceededException;
import de.triplet.gradle.play.TaskHelper;

public abstract class FileLengthValidator implements IValidator<File> {

    @Override
    public boolean validate(Project project, File asset) {
        try {
            TaskHelper.readAndTrimFile(project, asset, getMaxLength(), true);
            return true;
        } catch (LimitExceededException e) {
            return false;
        }
    }

    protected abstract int getMaxLength();
}
