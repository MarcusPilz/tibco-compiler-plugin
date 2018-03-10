package com.dbsystel.maven.plugins.tibco.deployment;

import java.io.File;
import java.util.ArrayList;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import com.dbsystel.maven.plugins.tibco.common.AbstractTibcoBwDeployMojo;

@Mojo(name = "deploy-bw", requiresDependencyResolution = ResolutionScope.RUNTIME, threadSafe = true,
        executionStrategy = "once-per-session")
public class TibcoBwDeployEarMojo extends AbstractTibcoBwDeployMojo implements
        com.dbsystel.maven.common.PropertyNames {

    @Parameter
    private boolean redeploy;

    /**
     * If true, the successfully deployed service insatnce won't be started. Default is false.
     */
    @Parameter(property = "install.nostart", defaultValue = "false")
    private boolean noStart;

    /**
     * If true, the service instance running before deployment won't stopped. Default is false.
     */
    @Parameter(property = "install.nonstop", defaultValue = "false")
    private boolean noStop;

    /**
     * If true, deploy service instance one at a time instead of in parallel. Default is true.
     */
    @Parameter(property = "install.serialize", defaultValue = "true")
    private boolean serialize;

    //@Parameter
    //private File delpoyConfigXML;

    @Override
    public String getInitMessage() {
        this.getLog().info("TibcoBwDeployMojo::getInitMessage");
        return DEPLOYING_APPLICATION + "'" + this.getOutputFile() + "'" + DEPLOYING_ON_DOMAIN + "'"
                + this.getDomainName() + "'";
    }

    @Override
    public String getFailureMessage() {
        return DEPLOY_EAR_FAILED;
    }

    @Override
    public void postAction() throws MojoExecutionException {
        this.getLog().info("TibcoBwDeployMojo::postAction():: NOTHING TO DO!!!");
    }

    @Override
    public ArrayList<String> arguments() {
        this.getLog().info("TibcoBwDeployMojo::arguments()");
        File ear = getOutputFile();
        if (ear == null || !ear.exists()) {
            MavenProject project = this.project;
            if (project != null && project.getBasedir() != null && project.getBasedir().exists()) {
                ear = getArtifactFile(new File(this.project.getBuild().getOutputDirectory()), finalName, classifier);
                this.getLog().debug("...adding ear..." + ear.getAbsolutePath());
            }
        }
        this.getLog().debug(USING_EAR + ear.getAbsolutePath());
        this.getLog().debug(deploymentDescriptorFinal.getAbsolutePath());

        ArrayList<String> args = new ArrayList<String>();
        args.add("-deploy");
        args.add("-ear");
        args.add(ear.getAbsolutePath());
        args.add("-deployConfig");
        args.add(deploymentDescriptorFinal.getAbsolutePath());
        args.add("-app");
        args.add(this.project.getArtifactId());
        if (this.serialize) {
            args.add("-serialize");
        }
        if (this.noStart) {
            args.add("-nostart");
        }
        if (this.noStop) {
            args.add("-nostop");
        }

        args.add("-force");
        args.addAll(super.defaultArguments());

        return args;
    }

    @Override
    public String goal() {
        return "deploy-bw";
    }

}
