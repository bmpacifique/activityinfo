package org.activityinfo.gcloud

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


class ArchiveArtifactTask extends DefaultTask {
    
    File archivePath
    String renameTo
    
    String getArchiveUrl() {
        if(renameTo) {
            return "gs://ai-pipeline/artifacts/${renameTo}"
        } else {
            return "gs://ai-pipeline/artifacts/${archivePath.name}"
        }
    }
    
    @TaskAction
    def archive() {
        project.exec {
            commandLine 'gsutil', 'cp', archivePath, archiveUrl
        }
    }
}
