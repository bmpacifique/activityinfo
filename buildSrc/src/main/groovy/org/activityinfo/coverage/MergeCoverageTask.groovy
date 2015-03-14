package org.activityinfo.coverage

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class MergeCoverageTask extends DefaultTask {
    
    @Input
    File gwtCoverageReportsDir
    
    @Input
    File jacocoReportFile
    
    @OutputFile
    File outputFile
    
    @TaskAction
    public void mergeReports() {
        
        ProjectCoverage coverage = new ProjectCoverage(project.rootProject)
        
        // Parse the GWT Coverage Reports
        GwtCoverageParser gwtCoverage = new GwtCoverageParser(coverage)
        gwtCoverageReportsDir.eachFile { it ->
            gwtCoverage.readGwtOutput(it)
        }
        
        // And the Jacoco reports..
        JacocoParser jacoco = new JacocoParser(coverage)
        jacoco.parse(jacocoReportFile)
        
        coverage.writeReport(outputFile)
    }
}
