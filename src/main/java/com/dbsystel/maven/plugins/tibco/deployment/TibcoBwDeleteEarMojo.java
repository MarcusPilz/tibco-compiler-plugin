package com.dbsystel.maven.plugins.tibco.deployment;

import java.util.ArrayList;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import com.dbsystel.maven.plugins.tibco.common.AbstractTibcoBwDeployMojo;

/**
 * 
 * <p>
 * This goal deletes a TIBCO BusinessWorks application from a TIBCO domain. The application will be undeployed before
 * deleted.
 * </p>
 *
 */
@Mojo(name = "delete-ear", defaultPhase = LifecyclePhase.DEPLOY, threadSafe = true)
public class TibcoBwDeleteEarMojo extends AbstractTibcoBwDeployMojo {
    protected final static String DELETE_EAR_FAILED = "The deletion of the application failed.";

    protected final static String DELETING_EAR = "Deleting the application...";

    @Override
    public String getInitMessage() {
        return DELETING_EAR;
    }

    @Override
    public String getFailureMessage() {
        return DELETE_EAR_FAILED;
    }

    @Override
    public void postAction() throws MojoExecutionException {
        this.getLog().info("TibcoBwDeleteEarMojo::NOTHING TO DO!!!");
    }

    @Override
    public ArrayList<String> arguments() {
        ArrayList<String> arguments = super.defaultArguments();
        arguments.add("-delete");
        arguments.add("-force"); // first undeploy

        return arguments;
    }

    @Override
    public String goal() {
        return "delete-ear";
    }
}
