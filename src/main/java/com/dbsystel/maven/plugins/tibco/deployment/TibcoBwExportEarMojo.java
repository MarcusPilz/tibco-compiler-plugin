package com.dbsystel.maven.plugins.tibco.deployment;

import java.util.ArrayList;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import com.dbsystel.maven.common.PropertyNames;
import com.dbsystel.maven.plugins.tibco.common.AbstractTibcoBwDeployMojo;

@Mojo(name = "export-bw-ear", defaultPhase = LifecyclePhase.SITE, threadSafe = true,
        requiresDependencyResolution = ResolutionScope.RUNTIME)
@Execute(phase = LifecyclePhase.SITE)
public class TibcoBwExportEarMojo extends AbstractTibcoBwDeployMojo {

    @Override
    public String getInitMessage() {
        // TODO Auto-generated method stub
        return PropertyNames.EXPORT_EAR_FROM_DOMAIN;
    }

    @Override
    public String getFailureMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void postAction() throws MojoExecutionException {
        // TODO Auto-generated method stub

    }

    @Override
    public ArrayList<String> arguments() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String goal() {
        // TODO Auto-generated method stub
        return null;
    }

}
