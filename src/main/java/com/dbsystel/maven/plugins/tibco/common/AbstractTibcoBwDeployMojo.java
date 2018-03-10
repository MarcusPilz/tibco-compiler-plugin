package com.dbsystel.maven.plugins.tibco.common;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.CallbackHandler;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.ShutdownHookProcessDestroyer;
import org.apache.commons.exec.launcher.CommandLauncher;
import org.apache.commons.exec.launcher.CommandLauncherFactory;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.crypto.DefaultSettingsDecrypter;
import org.apache.maven.settings.crypto.DefaultSettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecrypter;
import org.apache.maven.settings.crypto.SettingsDecryptionResult;
import org.apache.maven.shared.filtering.MavenFileFilter;
import org.apache.maven.shared.filtering.MavenResourcesFiltering;
import org.apache.maven.shared.utils.io.FileUtils.FilterWrapper;

import com.dbsystel.maven.common.PropertyNames;

public abstract class AbstractTibcoBwDeployMojo extends AbstractMojo implements PropertyNames {
    /**
     * The Maven project.
     * 
     * 
     */
    @Parameter(property = "project", defaultValue = "${project}", required = true, readonly = true)
    protected MavenProject project;
    
    @Parameter( defaultValue = "${reactorProjects}", required = true, readonly = true )
    private List<MavenProject> reactorProjects;
    
    /**
     * The current Maven session.
     * 
     * 
     *
     */
    @Parameter(property = "session", required = true, readonly = true)
    protected MavenSession session;

    /**
     * Provides a reference to the settings file.
     * 
     * 
     */
    @Parameter(property = "settings", readonly = true, required = true, defaultValue = "${settings}")
    private Settings settings;

    @Component(role = SettingsDecrypter.class)
    private DefaultSettingsDecrypter settingsDecrypter;

    /**
     * The character encoding scheme to be applied when filtering resources.
     */
    @Parameter(property = PropertyNames.SOURCE_ENCODING, defaultValue = "UTF-8")
    protected String encoding;

    @Parameter(property = PropertyNames.DEPLOYMENT_DESCRIPTOR)
    protected File deploymentDescriptor;

    @Parameter(property = PropertyNames.DEPLOYMENT_DESCRIPTOR_FINAL)
    protected File deploymentDescriptorFinal;

    @Parameter(property = PropertyNames.DEPLOYMENT_GLOBAL_VARIABLES)
    protected File deploymentGlobalVariables;

    @Parameter(property = PropertyNames.DEPLOYENT_COMMON_VARIABLES)
    protected File deploymentCommonVariables;

    @Parameter(property = PropertyNames.DEPLOYMENT_GLOBAL_VARIABLES_REFERENCE, required = true)
    protected File deploymentGlobalVariablesReference;

    @Parameter(property = PropertyNames.deploymentServicesProperty)
    protected File deploymentServices;

    @Parameter(property = PropertyNames.deploymentServicesCommonProperty)
    protected File deploymentServicesCommon;

    @Parameter(property = PropertyNames.DEPLOYMENT_GLOBAL_SERVICES_REFERENCE_PROPERTY, required = true)
    protected File deploymentGlobalServicesReference;

    private volatile CallbackHandler handler;

    /**
     * Specifies the id of the server if the username and password is to be retrieved from the settings.xml file
     * 
     * @readOnly
     */
    @Parameter(property = PropertyNames.ID)
    private String id;

    /**
     * Specifies the username to use if prompted to authenticate by the server.
     * 
     * If no username is specified and the server requests authentication the user will be prompted to supply the
     * username,
     * 
     * 
     */
    @Parameter(property = PropertyNames.USERNAME)
    protected String domainUsername;

    /**
     * Specifies the password to use if prompted to authenticate by the server.
     * 
     * If no password is specified and the server requests authentication the user will be prompted to supply the
     * password,
     * 
     *
     */
    @Parameter(property = PropertyNames.PASSWORD)
    protected String domainPassword;

    /**
     * Set this to <code>true</code> to bypass artifact installation
     * 
     * 
     */
    @Parameter(defaultValue = "false", property = PropertyNames.SKIP_INSTALL)
    private boolean skip;

    /**
     * 
     */
    @Parameter(property = PropertyNames.DIRECTORY, defaultValue = "${project.build.directory}")
    protected File directory;

    /**
     * Timeout for the execution of TIBCO commands. Default is 500ms.
     * 
     * @readOnly
     */
    @Parameter(property = PropertyNames.TIMEOUT)
    private int timeout = 500000;

    /**
     * Deployment Mode
     */
    public enum DeploymentMode {
        SIMPLE,
        COMPLEX,
        BATCH;
    }

    /**
     * 
     */
    @Parameter(property = PropertyNames.DEPLOYMENT_MODE, defaultValue = "SIMPLE")
    private DeploymentMode deploymentMode;

    /**
     * Path to the TIBCO home directory.
     * 
     * 
     */
    @Parameter(property = PropertyNames.TIBCO_HOME_DIR)
    protected File tibcoHome;

    /**
     * Used Tibco Version
     * 
     * 
     */
    @Parameter(property = PropertyNames.TIBCO_VERSION, required = false, defaultValue = "5.9")
    protected String tibcoVersion;

    /**
     * Path to the TIBCO "AppManage" binary.
     * 
     * 
     */
    @Parameter(property = PropertyNames.APPMANAGE_UTILITY_PATH)
    protected File tibcoAppManage;

    /**
     * 
     * @return the AppManage Utility
     */
    protected File getAppManageUtility() {
        return tibcoAppManage;
    }

    /**
     * Path to the TIBCO "AppManage" TRA configuration file.
     */
    @Parameter(property = PropertyNames.APPMANAGE_TRA_PATH, required = false)
    protected File tibcoAppManageTRAPath;

    /**
     * Path to the TIBCO Buildear Utility.
     */
    @Parameter(property = PropertyNames.BUILDEAR_UTILITY_PATH)
    protected File tibcoBuildear;

    /**
     * Path to the TIBCO "AppManage" TRA configuration file.
     */
    @Parameter(property = PropertyNames.BUILDEAR_TRA_PATH, required = false)
    protected File tibcoBuildearTRAPath;

    /**
     * Path to the TIBCO RendezVous folder.
     */
    @Parameter(property = "tibrv.home.path")
    protected File tibcoRvHomePath;

    // ==============================
    // Hawk configuration 
    // ==============================
    /**
     * Domain Name
     */
    @Parameter(property = PropertyNames.DOMAIN_NAME)
    protected String domainName;

    /**
     * Domain Name
     */
    @Parameter(property = PropertyNames.SERVER_URL, defaultValue = "localhost")
    protected String serverUrl;

    /**
     * Version of Hawk. Default is 5.1
     */
    @Parameter(property = PropertyNames.HAWK_VERSION, defaultValue = "5.1")
    protected String hawkVersion;

    @Parameter(property = "hawk.subscribe.interval", defaultValue = "10")
    protected Integer hawkSubscribeInterval; // in seconds

    @Parameter(property = "hawk.subscribe.retry.count", defaultValue = "30")
    protected Integer hawkSubscribeNumberOfRetry;

    @Parameter(property = "hawk.rv.service", defaultValue = "7474")
    protected String hawkRvService;

    @Parameter(property = "hawk.rv.network", defaultValue = ";")
    protected String hawkRvNetwork;

    @Parameter(property = "hawk.rv.daemon", defaultValue = "tcp:7474")
    protected String hawkRvDaemon;

    // =======================
    // Repo Parameters
    // =======================
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

    // =======================
    // Artifact specific information
    // =======================
    /**
     * Add classifier to the artifact
     */
    @Parameter(property = PropertyNames.FINALNAME)
    protected String finalName;

    /**
     * Add classifier to the artifact
     */
    @Parameter(property = PropertyNames.CLASSIFIER)
    protected String classifier;

    /**
     * The directory for the generated EAR.
     */
    @Parameter(defaultValue = "${project.build.directory}", required = true)
    private String outputDirectory;

    @Parameter(property = PropertyNames.IGNORE_REFERENCE_FILES, defaultValue = "false")
    protected boolean ignoreReferenceFiles;

    @Parameter(property = PropertyNames.IGNORE_COMMON_FILES, defaultValue = "false")
    protected boolean ignoreCommonFiles;

    /**
     */
    @Component(role = MavenFileFilter.class, hint = "default")
    protected MavenFileFilter mavenFileFilter;

    /**
     */
    @Component(role = MavenResourcesFiltering.class, hint = "default")
    private MavenResourcesFiltering mavenResourcesFiltering;

    protected List<FilterWrapper> filterWrappers;

    /**
     * This returns a Artifact object with the same groupId, artifactId, version and scope as the main
     * artifact of the project (for instance 'bw-ear' or 'projlib').
     * 
     * This  Artifact will have its own  type and  classifier.
     * 
     * @param a the artifact
     * @param type the type
     * @param classifier the classifier
     * @return Artifact the artifact
     */
    protected Artifact extractArtifact(Artifact a, String type, String classifier) {
        this.getLog().debug(this.getClass().getName() + " extractArtifact ...");
        if (a == null) {
            return a;
        }

        Artifact result = new DefaultArtifact(a.getGroupId(), a.getArtifactId(), a.getVersionRange(), a.getScope(),
                type, classifier, new DefaultArtifactHandler(type));

        return result;
    }

    /**
     * 
     * @param failIfNotFound true if the init of hawks breaks execution.
     * @return true if successful
     * @throws MojoExecutionException if error occur
     */
    protected boolean initHawk(boolean failIfNotFound) throws MojoExecutionException {
        getLog().info("AbstractTibcoBwMojo::initHawk()");
        if (tibcoRvHomePath == null || !tibcoRvHomePath.exists()) {
            if (failIfNotFound) {
                throw new MojoExecutionException(TIBCO_HAWK_BINARY_NOTFOUND);
            } else {
                getLog().info("Unable to init Hawk.");
                return false;
            }
        }

        File tibrvj;
        if (SystemUtils.IS_OS_WINDOWS) {
            getLog().debug("Windows OS");
            tibcoRvHomePath = new File(tibcoRvHomePath, "bin/");
            tibrvj = new File(tibcoRvHomePath, "tibrvj.dll");
            System.load(tibrvj.getAbsolutePath());
        } else {
            getLog().debug("Not Windows OS");
            tibcoRvHomePath = new File(tibcoRvHomePath, "lib/");
            String osArch = System.getProperty("os.arch");
            tibrvj = null;
            if (osArch.equals("x86")) {
                getLog().debug("x86");
                tibrvj = new File(tibcoRvHomePath, "libtibrvj.so");
                System.loadLibrary("tibrvj");
            } else if (osArch.contains("64")) {
                getLog().debug("64");
                tibrvj = new File(tibcoRvHomePath, "libtibrvj64.so");
                System.loadLibrary("tibrvj64");
            }
        }
        getLog().debug("Loading system library : " + tibrvj.getAbsolutePath());

        return true;
    }

    /**
     * TODO CHECK IF THIS PARAMETERS SHOULD BE CALLED EVERY APPMANAGE CMD
     * @return a list with common arguments for appmanage
     */
    public ArrayList<String> defaultArguments() {
        ArrayList<String> args = new ArrayList<String>();
        args.add("-domain");
        //args.add(serverUrl); 
        args.add(domainName);
        args.add("-user");
        args.add(domainUsername);
        args.add("-pw");
        args.add(domainPassword);

        return args;
    }

    public abstract String getInitMessage();

    public abstract String getFailureMessage();

    public abstract void postAction() throws MojoExecutionException;

    public abstract ArrayList<String> arguments();

    /**
     * The goal of the deployment.
     *
     * @return the goal of the deployment.
     */
    public abstract String goal();

    /**
     * Check Property Setting to AppManage Binary is correct
     * TODO move this method in the initialize mojo at dev-platform core
     * @throws MojoExecutionException if error occurs
     * @throws MojoFailureException if error occurs
     */
    protected void checkUtilities() throws MojoExecutionException, MojoFailureException {
        this.getLog().debug("Check Binarys... " + tibcoHome);

        String fileExtension = "";
        if (SystemUtils.IS_OS_WINDOWS) {
            fileExtension = ".exe";
        } else {
            fileExtension = "";
        }

        if (tibcoHome != null && tibcoAppManage == null) {
            tibcoAppManage = new File(tibcoHome + System.getProperty("file.separator") + "tra"
                    + System.getProperty("file.separator") + tibcoVersion + System.getProperty("file.separator")
                    + "bin" + System.getProperty("file.separator") + PropertyNames.APPMANAGE_UTILITY + fileExtension);
            this.getLog().debug("AppManage Utility : " + tibcoAppManage);

        }

        if (tibcoHome != null && tibcoBuildear == null) {
            tibcoBuildear = new File(tibcoHome + System.getProperty("file.separator") + "tra"
                    + System.getProperty("file.separator") + tibcoVersion + System.getProperty("file.separator")
                    + "bin" + System.getProperty("file.separator") + PropertyNames.BUILDEAR_UTILITY + fileExtension);
            this.getLog().debug("BuildEar Utility : " + tibcoBuildear);

        }

        if (tibcoAppManage == null || !tibcoAppManage.exists() || !tibcoAppManage.isFile() || tibcoBuildear == null
                || !tibcoBuildear.exists() || !tibcoBuildear.isFile()) {
            throw new MojoExecutionException(TIBCO_UTILITY_BINARY_NOTFOUND);
        }
    }

    /**
     * Indicates whether or not the goal should be skipped.
     *
     * @return {@code true} to skip the goal, otherwise {@code false}
     */
    protected boolean isSkip() {
        return skip;
    }

    /**
     * The domainname to deploy the archive to.
     *
     * @return the domainname of the server.
     */
    public final String domainname() {
        return domainName;
    }

    /**
     * The Server is configured in settings.xml
     */
    private void getCredentialsFromSettings() {
        if (settings != null) {
            Server server = settings.getServer(id);
            if (server != null) {
                getLog().debug(DEBUG_MESSAGE_SETTINGS_HAS_ID);
                domainPassword = decrypt(server);
                domainUsername = server.getUsername();
                if (domainUsername != null && domainPassword != null) {
                    getLog().debug(DEBUG_MESSAGE_SETTINGS_HAS_CREDS);
                } else {
                    getLog().debug(DEBUG_MESSAGE_NO_CREDS);
                }
            } else {
                getLog().debug(DEBUG_MESSAGE_NO_SERVER_SECTION);
            }
        } else {
            getLog().debug(DEBUG_MESSAGE_NO_SETTINGS_FILE);
        }
    }

    /**
     * The Passwort is crypted in settings.xmml
     * 
     * @param server
     * @return
     */
    private String decrypt(final Server server) {
        SettingsDecryptionResult decrypt = settingsDecrypter.decrypt(new DefaultSettingsDecryptionRequest(server));
        return decrypt.getServer().getPassword();
    }

    /**
     * Retrieves the full path of the artifact that will be created.
     * 
     * @param basedir
     *            , the directory where the artifact will be created
     * @param finalName
     *            , the name of the artifact, without file extension
     * @param classifier, the classifier if the project
     * @return a {@link File} object with the path of the artifact
     */
    protected File getArtifactFile(File basedir, String finalName, String classifier) {
        if (classifier == null) {
            classifier = "";
        } else if (classifier.trim().length() > 0 && !classifier.startsWith("-")) {
            classifier = "-" + classifier;
        }

        return new File(directory, finalName + classifier + ".ear");
    }

    /**
     * @return the Maven artefact as a {@link File}
     */
    protected File getOutputFile() {
        return getArtifactFile(directory, finalName, classifier);
    }

    /**
     * This calls a TIBCO binary.
     * 
     * @param binary
     *            , the TIBCO binary file to execute
     * @param tras
     *            , the TRA files associated with the TIBCO binary
     * @param arguments
     *            , command-line arguments
     * @param workingDir
     *            , working directory from where the binary is launched
     * @param errorMsg
     *            , error message to display in case of a failure
     * @param fork
     *            , if true the child process will be detached from the caller
     * @param synchronous
     *            , if true the child process will be executed synchrounous           
     *            
     * @return 0 if successful, else lower or greater 0
     * @throws MojoExecutionException if error occur
     */
    protected int launchTIBCOBinary(File binary, List<File> tras, ArrayList<String> arguments, File workingDir,
            String errorMsg, boolean fork, boolean synchronous) throws MojoExecutionException {
        this.getLog().debug("...launch..." + binary.getAbsolutePath() + "...Execute Goal : " + goal());
        Integer result = 0;

        CommandLine cmdLine = new CommandLine(binary);
        ArrayList<String> args = arguments();
        // no value specified as Mojo parameter, we use the .tra in the same directory as the binary

        if (tras == null) {
            String traPathFileName = binary.getAbsolutePath();
            traPathFileName = FilenameUtils.removeExtension(traPathFileName);
            traPathFileName += ".tra";
            args.add("--propFile");
            args.add(traPathFileName);
        }

        for (String argument : args) {
            cmdLine.addArgument(argument);
        }

        getLog().debug("...launchTIBCOBinary command line : " + cmdLine.toString());

        DefaultExecutor executor = new DefaultExecutor();
        executor.setWorkingDirectory(workingDir);
        //executor.setExitValue(1);

        if (timeout > 0) {
            ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout);
            executor.setWatchdog(watchdog);
        }

        executor.setProcessDestroyer(new ShutdownHookProcessDestroyer());

        try {
            if (fork) {
                this.getLog().debug(this.getClass().getName() + " using fork...");
                CommandLauncher commandLauncher = CommandLauncherFactory.createVMLauncher();

                commandLauncher.exec(cmdLine, null, workingDir);

            } else {

                if (synchronous) {
                    result = executor.execute(cmdLine);
                } else {
                    executor.execute(cmdLine, new DefaultExecuteResultHandler());
                }

            }

            getLog().info("RetCode :: " + result.toString());
        } catch (IOException e) {
            this.getLog().debug("UPS... ERROR OCCURED..." + e.getMessage());
            throw new MojoExecutionException(e.getMessage(), e);
        }
        return result;
    }

    /**
     * Same as launchTIBCOBinary with 'fork=false' and 'synchronous=true'
     * @param binary
     *            , the TIBCO binary file to execute
     * @param tras
     *            , the TRA files associated with the TIBCO binary
     * @param arguments
     *            , command-line arguments
     * @param workingDir
     *            , working directory from where the binary is launched
     * @param errorMsg
     *            , error message to display in case of a failure
     * 
     * @throws MojoExecutionException if error occur
     * 
     */
    protected void launchTIBCOBinary(File binary, List<File> tras, ArrayList<String> arguments, File workingDir,
            String errorMsg) throws MojoExecutionException {
        launchTIBCOBinary(binary, tras, arguments, workingDir, errorMsg, false, true);
    }

    /**
     * @throws MojoExecutionException if error occur
     * @throws MojoFailureException if error occur
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (isSkip()) {
            getLog().debug(
                    String.format("Skipping deployment of %s:%s", project.getGroupId(), project.getArtifactId()));
            return;
        }
        try {
            init();
            this.getLog().info(getInitMessage());
            ArrayList<String> arguments = arguments();
            launchTIBCOBinary(tibcoAppManage, null, arguments, directory, getFailureMessage());
            postAction();

        } catch (Exception e) {
            throw new MojoExecutionException(String.format("Could not execute goal %s on %s. Reason: %s", goal(),
                    project.getGroupId(), project.getArtifactId(), e.getMessage()), e);
        }
    }

    protected String getDomainUsername() {
        return domainUsername;
    }

    protected void setDomainUsername(String domainUsername) {
        this.domainUsername = domainUsername;
    }

    protected String getDomainPassword() {
        return domainPassword;
    }

    protected void setDomainPassword(String domainPassword) {
        this.domainPassword = domainPassword;
    }

    protected String getDomainName() {
        return domainName;
    }

    protected void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getOutputDirectory() {
        return outputDirectory != null ? outputDirectory : this.project.getBuild().getOutputDirectory();
    }

    public String getFinalName() {
        return finalName != null ? finalName : this.project.getBuild().getFinalName();
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * 
     * @throws MojoExecutionException if error occur
     * @throws MojoFailureException if error occur
     */
    protected void init() throws MojoExecutionException, MojoFailureException {
        if (this.getLog().isDebugEnabled()) {
            this.getLog().debug("Initialize ..." + this.getClass().getName());           
        }
        String profileId = null;
        List<org.apache.maven.model.Profile> profiles = this.project.getActiveProfiles();
        for (org.apache.maven.model.Profile profile : profiles) {
            this.getLog().debug("... active Profiles..." + profile.getId());
            switch (profile.getId()) {
            case "dev":
                profileId = "DEV";
                break;
            case "int":
                profileId = "INT";
                break;
            case "tst":
                profileId = "TST";
                break;
            case "abn":
                profileId = "ABN";
                break;
            case "prd":
                profileId = "PRD";
                break;
            }
        }
        checkUtilities();
        getCredentialsFromSettings();
        if (deploymentDescriptorFinal == null) {
            this.getLog().debug(
                    "...set param final application deployment descriptor because param "
                            + PropertyNames.DEPLOYMENT_DESCRIPTOR_FINAL + " is not set");
            if (profileId != null) {
                deploymentDescriptorFinal = new File(project.getBuild().getOutputDirectory() + File.separator
                        + profileId + "-" + project.getBuild().getFinalName() + "-final.xml");

            } else {
                deploymentDescriptorFinal = new File(project.getBuild().getOutputDirectory() + File.separator + "-"
                        + project.getBuild().getFinalName() + "-final.xml");
            }
            //deploymentDescriptorFinal.createNewFile();
        }
        if (deploymentDescriptor == null) {
            this.getLog().debug(
                    "...set param a application deployment descriptor because param "
                            + PropertyNames.DEPLOYMENT_DESCRIPTOR + " is not set");
            if (profileId != null) {
                deploymentDescriptor = new File(project.getBuild().getOutputDirectory() + File.separator + profileId
                        + "-" + project.getBuild().getFinalName() + ".xml");

            } else {
                deploymentDescriptor = new File(project.getBuild().getOutputDirectory() + File.separator + "-"
                        + project.getBuild().getFinalName() + ".xml");
            }
            //deploymentDescriptor.createNewFile();
        }
        if (deploymentGlobalVariables == null) {
            deploymentGlobalVariables = new File(project.getBuild().getOutputDirectory() + File.separator
                    + "deploymentGlobalVariables.properties");
        }
        if (deploymentServices == null) {
            deploymentServices = new File(project.getBuild().getOutputDirectory() + File.separator
                    + "deploymentServices.properties");
        }
        getLog().debug("deploymentDescriptor ::" + deploymentDescriptor.getAbsolutePath());
        getLog().debug("deploymentDescriptor ::" + deploymentDescriptorFinal.getAbsolutePath());

    }
}
