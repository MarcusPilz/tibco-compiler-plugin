package com.dbsystel.maven.plugins.tibco.deployment;

import java.util.ArrayList;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import com.dbsystel.maven.plugins.tibco.common.AbstractTibcoBwDeployMojo;

/**
 * 
 * <p>
 * This goal stops a TIBCO BusinessWorks application deployed on a TIBCO domain.
 * </p>
 * 
 */
@Mojo(name = "stop-bw", defaultPhase = LifecyclePhase.DEPLOY, threadSafe = true)
public class TibcoBwStopEarMojo extends AbstractTibcoBwDeployMojo {
    protected final static String STOP_EAR_FAILED = "Some instances failed to be stopped.";

    protected final static String STOPPING_EAR = "Stopping instances of the application...";

    @Override
    public String getInitMessage() {
        return STOPPING_EAR;
    }

    @Override
    public String getFailureMessage() {
        return STOP_EAR_FAILED;
    }

    @Override
    public void postAction() throws MojoExecutionException {
        this.getLog().info("TibcoBwStopEarMojo::postAction::NOT IMPLEMENTED!!");

    }

    @Override
    public ArrayList<String> arguments() {
        ArrayList<String> arguments = super.defaultArguments();
        arguments.add("-stop");

        return arguments;
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (isSkip()) {
            getLog().debug(String.format("Skipping Stopping of %s:%s", project.getGroupId(), project.getArtifactId()));
        }

    }

    @Override
    public String goal() {
        return "stop-bw";
    }

}
