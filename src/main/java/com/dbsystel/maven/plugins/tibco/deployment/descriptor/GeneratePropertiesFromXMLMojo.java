package com.dbsystel.maven.plugins.tibco.deployment.descriptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.settings.Settings;
import org.apache.maven.shared.filtering.MavenResourcesFiltering;
import org.jdom2.JDOMException;

import com.dbsystel.maven.common.PropertyNames;
import com.dbsystel.maven.plugins.tibco.common.AbstractTibcoBwDeployMojo;
import com.dbsystel.platform.dev.descriptors.tibco.ApplicationManagement;
import com.dbsystel.platform.dev.descriptors.tibco.application.management.Event;


@Mojo(name = "generate-properties", defaultPhase = LifecyclePhase.INSTALL, threadSafe = true,
        executionStrategy = "once-per-session", requiresDependencyResolution = ResolutionScope.TEST)
public class GeneratePropertiesFromXMLMojo extends AbstractTibcoBwDeployMojo implements PropertyNames {

    @Parameter(property = "ignoreReferenceFiles", defaultValue = "false")
    protected boolean ignoreReferenceFiles;

    @Parameter(property = "ignoreCommonFiles", defaultValue = "false")
    protected boolean ignoreCommonFiles;

    @Parameter(property = "install.filterProperties", defaultValue = "false", alias = "filterProperties")
    protected boolean filterProperties;

    // repoInstance
    @Parameter(property = "repoSelectInstance", defaultValue = "local")
    protected String repoSelectInstance;

    @Parameter(property = "repoHttpTimeout", defaultValue = "600")
    protected Integer repoHttpTimeout;

    @Parameter(property = "repoHttpUrl", defaultValue = "")
    protected String repoHttpUrl;

    @Parameter(property = "repoHttpServer", defaultValue = "")
    protected String repoHttpServer;

    @Parameter(property = "repoHttpUser", defaultValue = "")
    protected String repoHttpUser;

    @Parameter(property = "repoHttpPassword", defaultValue = "")
    protected String repoHttpPassword;

    @Parameter(property = "repoHttpExtraPropertyFile", defaultValue = "")
    protected String repoHttpExtraPropertyFile;

    @Parameter(property = "repoRvTimeout", defaultValue = "600")
    protected Integer repoRvTimeout;

    @Parameter(property = "repoRvDiscoveryTimeout", defaultValue = "10")
    protected Integer repoRvDiscoveryTimeout;

    @Parameter(property = "repoRvDaemon", defaultValue = "tcp:7500")
    protected String repoRvDaemon;

    @Parameter(property = "repoRvService", defaultValue = "7500")
    protected String repoRvService;

    @Parameter(property = "repoRvNetwork", defaultValue = "")
    protected String repoRvNetwork;

    @Parameter(property = "repoRvRegionalSubject", defaultValue = "")
    protected String repoRvRegionalSubject;

    @Parameter(property = "repoRvOperationRetry", defaultValue = "0")
    protected Integer repoRvOperationRetry;

    @Parameter(property = "repoRvServer", defaultValue = "")
    protected String repoRvServer;

    @Parameter(property = "repoRvUser", defaultValue = "")
    protected String repoRvUser;

    @Parameter(property = "repoRvPassword", defaultValue = "")
    protected String repoRvPassword;

    @Parameter(property = "repoRvExtraPropertyFile", defaultValue = "")
    protected String repoRvExtraPropertyFile;

    @Parameter(property = "repoLocalEncoding", defaultValue = "UTF-8")
    protected String repoLocalEncoding;

    //

    // monitor (applied to all <bw> services)
    @Parameter(property = "events", alias = "events")
    protected Event events;

    @Parameter(property = "install.maxDeploymentRevision", defaultValue = "-1")
    protected String maxDeploymentRevision;

    @Parameter(property = "install.contact", defaultValue = "")
    protected String contact;

    @Parameter(property = "install.description", defaultValue = "")
    protected String description;

    /**
     * Provides a reference to the settings file.
     * 
     */
    @Parameter(property = "settings", readonly = true, required = true, defaultValue = "${settings}")
    private Settings settings;

    /**
     * 
     * This loads a properties file into a java.util.Properties object.
     *  
     * @param propertiesFile a file which contains properties
     * @return properties the properties defined in the file
     * @throws IOException if error occur
     */
    protected Properties loadPropertiesFile(File propertiesFile) throws IOException {
        this.getLog().debug(
                "GeneratePropertiesFromXmlMojo : Load Properties from : " + propertiesFile.getAbsolutePath());
        Properties properties = new Properties();
        FileInputStream fileInputStream = new FileInputStream(propertiesFile);
        properties.load(fileInputStream);

        return properties;
    }

    @Component(role = org.apache.maven.shared.filtering.MavenResourcesFiltering.class, hint = "default")
    protected MavenResourcesFiltering mavenResourcesFiltering;

    /**
     * 
     * Save a java.util.Properties to a file.
     * 
     * @param outputFile
     *            , the File where to output the Properties
     * @param properties
     *            , the Properties to save
     * @param propertiesComment
     *            , the comment to add at the beginning of the file
     * @param success
     *            , the success message
     * @param failure
     *            , the failure message
     * @throws MojoExecutionException if error occur
     */
    protected void savePropertiesToFile(File outputFile, Properties properties, String propertiesComment,
            String success, String failure) throws MojoExecutionException {

        this.getLog().debug("Save Properties To... " + outputFile.getAbsolutePath());
        
        OutputStream outputStream = null;

        try {
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }
            outputStream = new FileOutputStream(outputFile);
            // Properties should only contain Strin
            properties.store(outputStream, propertiesComment);
            getLog().info(success + " '" + outputFile + "'");
        } catch (IOException e) {
            throw new MojoExecutionException(failure + " '" + outputFile + "'", e);
        } catch (ClassCastException e) {
            throw new MojoExecutionException(failure + " '" + outputFile + "'", e);
        } catch (NullPointerException e) {
            throw new MojoExecutionException(failure + " '" + outputFile + "'", e);
        } finally {
            try {
                outputStream.close();
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        this.getLog().info(getInitMessage());
        init();
        Properties earGlobalVariables = new Properties();
        Properties earServices = new Properties();

        try {
            ApplicationManagement application = new ApplicationManagement(deploymentDescriptor);

            earGlobalVariables = application.getGlobalVariables();
            this.getLog().debug("Global Variable read...");
            savePropertiesToFile(deploymentGlobalVariables, earGlobalVariables, "Global Variables",
                    PROPERTIES_SAVE_SERVICES_SUCCESS, PROPERTIES_SAVE_SERVICES_FAILURE);

            earServices = application.getServices().toProperties();
            savePropertiesToFile(deploymentServices, earServices, "Services (Bindings, Processes)",
                    PROPERTIES_SAVE_GVS_SUCCESS, PROPERTIES_SAVE_GVS_FAILURE);
            this.getLog().debug("Services read...");
            getLog().info(XML_LOAD_SUCCESS + " '" + deploymentDescriptor + "'");
        } catch (JDOMException e) {
            throw new MojoExecutionException(XML_LOAD_FAILURE + " '" + deploymentDescriptor + "'", e);
        } catch (IOException e) {
            throw new MojoExecutionException(XML_LOAD_FAILURE + " '" + deploymentDescriptor + "'", e);
        }
    }

    @Override
    protected void init() throws MojoExecutionException, MojoFailureException {
        super.init();
    }

    @Override
    public String getInitMessage() {
        return "generate-properties";
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
        return "generate-properties";
    }

}
