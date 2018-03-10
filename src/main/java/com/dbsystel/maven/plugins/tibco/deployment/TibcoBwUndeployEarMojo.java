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
 * This goal undeploys a TIBCO BusinessWorks application from a TIBCO domain.
 * </p>
 */
@Mojo(name = "undeploy", defaultPhase = LifecyclePhase.DEPLOY)
public class TibcoBwUndeployEarMojo extends AbstractTibcoBwDeployMojo {
    protected final static String UNDEPLOY_EAR_FAILED = "The undeployment of the application failed.";

    protected final static String UNDEPLOYING_EAR = "Undeploying the application...";

    @Override
    public String getInitMessage() {
        return UNDEPLOYING_EAR;
    }

    @Override
    public String getFailureMessage() {
        return UNDEPLOY_EAR_FAILED;
    }

    @Override
    public void postAction() throws MojoExecutionException {
        this.getLog().info("TibcoBwUndeplyEarMojo::NOTHING TO DO!!");

    }

    @Override
    public ArrayList<String> arguments() {
        ArrayList<String> arguments = super.defaultArguments();
        arguments.add("-undeploy");

        return arguments;
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (isSkip()) {
            getLog().debug(String.format("Skipping Undeploy of %s:%s", project.getGroupId(), project.getArtifactId()));
        }

    }

    @Override
    public String goal() {
        return "undeploy";
    }
}
