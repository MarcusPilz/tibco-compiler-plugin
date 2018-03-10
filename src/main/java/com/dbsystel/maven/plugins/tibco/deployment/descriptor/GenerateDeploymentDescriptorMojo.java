package com.dbsystel.maven.plugins.tibco.deployment.descriptor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.codehaus.plexus.util.FileUtils;
import org.jdom2.JDOMException;

import com.dbsystel.maven.common.PropertyNames;
import com.dbsystel.maven.plugins.tibco.common.AbstractTibcoBwDeployMojo;
import com.dbsystel.platform.dev.descriptors.tibco.ApplicationManagement;

/**
 * <p>
 * This goal will merge the reference and common properties into the working properties files.
 * </p>
 */
@Mojo(name = "generate-deployment-descriptor", defaultPhase = LifecyclePhase.INSTALL, threadSafe = true,
        executionStrategy = "once-per-session", requiresDependencyResolution = ResolutionScope.TEST)
public class GenerateDeploymentDescriptorMojo extends AbstractTibcoBwDeployMojo {
    protected ApplicationManagement application;

    protected static final String FLAT_PATH_SEPARATOR = "/";

    protected final static String XML_EXTENSION = ".xml";

    public final static String XML_TYPE = "xml";

    protected final static String WARN_NO_ARTIFACT_ATTACHED = "Could not attach artifact.";

    /**
     * Whether to "touch" the final deployment descriptor file when final deployment descriptor generation is skipped.
     * 
     */
    @Parameter(property = "install.descriptorFinalTouch", required = false, defaultValue = "false")
    protected Boolean touchFinalDeploymentDescriptorIfSkipped;

    // monitor (applied to all <bw> services)
    //@Parameter(property = "events", alias = "events")
    //protected Events events;

    @Parameter(property = "install.maxDeploymentRevision", defaultValue = "-1")
    protected String maxDeploymentRevision;

    @Parameter(property = "install.contact", defaultValue = "")
    protected String contact;

    @Parameter(property = "install.description", defaultValue = "")
    protected String description;

    /**
     * By default, the empty binding ("bw[EXAMPLE.par]/bindings/binding[]) is removed if other named bindings exists
     * ("bw[EXAMPLE.par]/bindings/binding[named]"). It is possible to keep this empty binding by setting
     * <i>alwaysKeepEmptyBindings</i> to <i>true</i>. Default is <i>false</i>.
     */
    @Parameter(property = "alwaysKeepEmptyBindings", defaultValue = "false")
    boolean alwaysKeepEmptyBindings;

    /**
     * It is possible to ignore any merging of properties by setting <i>ignorePropertiesMerge</i> to <i>true</i>.
     * Default is <i>false</i> (do merge).
     */
    @Parameter(property = "ignorePropertiesMerge", defaultValue = "false")
    boolean ignorePropertiesMerge;

    @Parameter(property = "repoLocalEncoding", defaultValue = "UTF-8")
    protected String repoLocalEncoding;

    /**
     * By default the properties from common properties are merged first and the properties from reference specific to
     * the project last. By setting <i>mergeCommonLast</i> to true, the common properties are mergerd last.
     */
    @Parameter(property = "mergeCommonLast", defaultValue = "false")
    boolean mergeCommonLast;

    /**
     * 
     */
    @Parameter(property = "filterProperties", defaultValue = "false")
    protected boolean filterProperties;

    /**
     * updateMonitoringRules
     */
    /*
     * private void updateMonitoringRules() { this.getLog().debug(this.getClass().getName() +
     * " updateMonitoringRules..."); Events events = createMonitoringEvents();
     * //application.addMonitoringEventsToAllServices(events); }
     */
    /*
     * private Events createMonitoringEvents() { this.getLog().debug(this.getClass().getName() +
     * " createMonitoringEvents ..."); Events result = new Events();
     * 
     * if (events != null) { List<FailureEvent> failures = events.getFailures(); if (failures != null) { for
     * (FailureEvent failureEvent : failures) { ApplicationManagement.addEvent(result, failureEvent); } } List<LogEvent>
     * logs = events.getLogs(); if (logs != null) { for (LogEvent logEvent : logs) {
     * ApplicationManagement.addEvent(result, logEvent); } } }
     * 
     * return result; }
     */

    /**
     * updateAdditionalInfo
     */
    private void updateAdditionalInfo(ApplicationManagement application) {
        getLog().debug("...Updating additional information...");
        getLog().debug("MaxDeploymentRevision: " + maxDeploymentRevision);
        getLog().debug("Contact: " + contact);
        getLog().debug("Description: " + description);
        application.setName(this.project.getArtifactId());
        //application.setMaxDeploymentRevision(maxDeploymentRevision);
        application.setContact(contact);
        //application.setDescription(this.project.getName());
        //application.setRepoInstanceName(domainName + "-" + this.project.getArtifactId());
    }

    protected void attachFile(File f, String type, String classifier) {
        this.getLog().debug(this.getClass().getName() + " attachFile ...");
        Artifact artifact = extractArtifact(this.project.getArtifact(), type, classifier);
        artifact.setFile(f);
        this.project.addAttachedArtifact(artifact);
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if (super.isSkip()) {
            this.getLog().info(SKIP_MESSAGE);
            return;
        }
        this.getLog().debug(this.getClass().getName() + " ...execute...." + goal());

        try {
            init();

            /**
             * 1. load given global variables
             */
            if (deploymentGlobalVariables != null && deploymentGlobalVariables.exists()) {
                getLog().debug(goal() + "... merging..." + deploymentGlobalVariables.getAbsolutePath());
                try {
                    application.merge(deploymentGlobalVariables);
                } catch (IOException e) {
                    throw new MojoExecutionException(PropertyNames.APPLICATION_MANAGEMENT_MERGE_FAILURE, e);
                } catch (IllegalArgumentException e) {
                    throw new MojoExecutionException(PropertyNames.APPLICATION_MANAGEMENT_MERGE_FAILURE, e);
                }
            }
            /**
             * 2. laod given common variables
             */
            if (deploymentCommonVariables != null && deploymentCommonVariables.exists()) {
                getLog().debug(goal() + "... merging..." + deploymentCommonVariables.getAbsolutePath());
                try {

                    application.merge(deploymentCommonVariables);
                } catch (IOException e) {
                    throw new MojoExecutionException(PropertyNames.APPLICATION_MANAGEMENT_MERGE_FAILURE, e);
                } catch (IllegalArgumentException e) {
                    throw new MojoExecutionException(PropertyNames.APPLICATION_MANAGEMENT_MERGE_FAILURE, e);
                }
            }
            /**
             * 3. load given references for global variables
             */
            if (deploymentGlobalVariablesReference != null && deploymentGlobalVariablesReference.exists()) {
                getLog().debug(goal() + "... merging..." + deploymentGlobalVariablesReference.getAbsolutePath());
                try {
                    application.merge(deploymentGlobalVariablesReference);
                } catch (IOException e) {
                    throw new MojoExecutionException(PropertyNames.APPLICATION_MANAGEMENT_MERGE_FAILURE, e);
                } catch (IllegalArgumentException e) {
                    throw new MojoExecutionException(PropertyNames.APPLICATION_MANAGEMENT_MERGE_FAILURE, e);
                }
            }
            /**
             * 4. load given services definitions
             */
            if (deploymentServices != null && deploymentServices.exists()) {
                getLog().debug(goal() + "... merging..." + deploymentServices.getAbsolutePath());
                try {
                    application.merge(deploymentServices);
                } catch (IOException e) {
                    throw new MojoExecutionException(PropertyNames.APPLICATION_MANAGEMENT_MERGE_FAILURE, e);
                } catch (IllegalArgumentException e) {
                    throw new MojoExecutionException(PropertyNames.APPLICATION_MANAGEMENT_MERGE_FAILURE, e);
                }
            }
            /**
             * 5. load given global service references
             */
            if (deploymentGlobalServicesReference != null && deploymentGlobalServicesReference.exists()) {
                getLog().debug(goal() + "... merging..." + deploymentGlobalServicesReference.getAbsolutePath());
                try {
                    application.merge(deploymentGlobalServicesReference);
                } catch (IOException e) {
                    throw new MojoExecutionException(PropertyNames.APPLICATION_MANAGEMENT_MERGE_FAILURE, e);
                } catch (IllegalArgumentException e) {
                    throw new MojoExecutionException(PropertyNames.APPLICATION_MANAGEMENT_MERGE_FAILURE, e);
                }
            }

            //updateRepoInstances(application);
            updateAdditionalInfo(application);
            application.write(this.deploymentDescriptorFinal);
            getLog().debug("generate final deployment descriptor... " + deploymentDescriptorFinal.getAbsolutePath());
            this.getLog().info(
                    PropertyNames.APPLICATION_MANAGEMENT_MERGE_SUCCESS + deploymentDescriptorFinal.getAbsolutePath());
        } catch (Exception e) {
            throw new MojoExecutionException(PropertyNames.APPLICATION_MANAGEMENT_MERGE_FAILURE, e);
        }

    }

    /**
     * @throws MojoFailureException if error occur
     * @throws MojoFailureException if error occur
     * 
     */
    @Override
    protected void init() throws MojoExecutionException, MojoFailureException {
        this.getLog().info(getInitMessage());
        try {
            super.init();

            getLog().debug("GlobalVariableReference ::" + deploymentGlobalVariablesReference.getAbsolutePath());
            getLog().debug("GlobalServiceReference ::" + deploymentGlobalServicesReference.getAbsolutePath());
            getLog().debug("deploymentDescriptor ::" + deploymentDescriptor.getAbsolutePath());
            getLog().debug("finaldeploymentDescriptor ::" + deploymentDescriptorFinal.getAbsolutePath());
            if (!deploymentDescriptor.exists()) {
                throw new MojoExecutionException(deploymentDescriptor.getAbsolutePath() + " does not exist!");
            }
            FileUtils.copyFile(deploymentDescriptor, deploymentDescriptorFinal);
            application = new ApplicationManagement(deploymentDescriptorFinal);

        } catch (IOException e) {
            throw new MojoExecutionException("FAIL TO OPEN " + deploymentDescriptor.getAbsolutePath());
        } catch (JDOMException e) {
            throw new MojoExecutionException("FAIL TO READ " + deploymentDescriptor.getAbsolutePath());
        }
    }

    @Override
    public String getInitMessage() {
        return "initialize generate-deployment-descriptor ...";
    }

    @Override
    public String getFailureMessage() {
        return "generate-deployment-descriptor failed....";
    }

    @Override
    public void postAction() throws MojoExecutionException {
        this.getLog().info("Nothing to do...");

    }

    @Override
    public ArrayList<String> arguments() {
        return null;
    }

    @Override
    public String goal() {
        return "generate-deployment-descriptor";
    }

}
