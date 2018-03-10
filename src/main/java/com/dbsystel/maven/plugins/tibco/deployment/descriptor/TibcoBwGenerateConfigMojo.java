package com.dbsystel.maven.plugins.tibco.deployment.descriptor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import com.dbsystel.maven.common.PropertyNames;
import com.dbsystel.maven.plugins.tibco.common.AbstractTibcoBwDeployMojo;

@Mojo(name = "generate-config", defaultPhase = LifecyclePhase.INSTALL, threadSafe = true,
        requiresDependencyResolution = ResolutionScope.RUNTIME)
public class TibcoBwGenerateConfigMojo extends AbstractTibcoBwDeployMojo {

    @Override
    public String getInitMessage() {
        return "generate-config";
    }

    @Override
    public String getFailureMessage() {
        // TODO Auto-generated method stub
        return PropertyNames.CREATE_XML_FROM_EAR_FAILED;
    }

    @Override
    public void postAction() throws MojoExecutionException {
        this.getLog().info(PropertyNames.CREATE_XML_FROM_EAR_SUCCESS + "'" + deploymentDescriptor + "'");
    }

    public ArrayList<String> arguments() {
        ArrayList<String> args = new ArrayList<String>();
        String earPath = this.project.getBuild().getOutputDirectory();
        String xmlOutputFile = deploymentDescriptor.getAbsolutePath();
        args.add("-export");
        args.add("-ear");
        args.add(earPath + File.separator + this.project.getBuild().getFinalName() + ".ear");
        args.add("-out");
        args.add(xmlOutputFile);
        return args;
    }

    /**
     * @throws MojoFailureException if error ocurrs via initialization
     * 
     */
    @Override
    protected void init() throws MojoExecutionException, MojoFailureException {
        this.getLog().info(getInitMessage());
        super.init();
        try {          
            this.launchTIBCOBinary(getAppManageUtility(), null, arguments(), new File(this.project.getBuild()
                    .getOutputDirectory()), null);
        } catch (Exception e) {
            throw new MojoExecutionException("Error generating config", e);
        }

    }

    @Override
    public String goal() {
        return "generate-config";
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        this.getLog().debug(this.getInitMessage());
        init();
    }

}
