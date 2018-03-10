package com.dbsystel.maven.plugins.tibco.deployment;

import java.util.ArrayList;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import com.dbsystel.maven.plugins.tibco.common.AbstractTibcoBwDeployMojo;

/**
 * 
 * <p>
 * This goal kills a TIBCO BusinessWorks application deployed on a TIBCO domain.
 * </p>
 *
 */
@Mojo(name = "kill-bw", defaultPhase = LifecyclePhase.DEPLOY)
@Execute(phase = LifecyclePhase.DEPLOY)
public class TibcoBwKillEarMojo extends AbstractTibcoBwDeployMojo {
    protected final static String KILL_EAR_FAILED = "Some instances failed to be killed.";

    protected final static String KILLING_EAR = "Killing instances of the application...";

    @Override
    public String getInitMessage() {
        return KILLING_EAR;
    }

    @Override
    public String getFailureMessage() {
        return KILL_EAR_FAILED;
    }

    @Override
    public void postAction() throws MojoExecutionException {
        this.getLog().info("TibcoBwKillEarMojo:NOTHIN TO DO!!!");

    }

    @Override
    public ArrayList<String> arguments() {
        ArrayList<String> arguments = super.defaultArguments();
        arguments.add("-kill");

        return arguments;
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (isSkip()) {
            getLog().debug(String.format("Skipping Killing of %s:%s", project.getGroupId(), project.getArtifactId()));
        }

    }

    @Override
    public String goal() {
        return "kill-bw";
    }

}
