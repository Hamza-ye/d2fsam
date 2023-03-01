package org.nmcpye.am.external.location;

import org.nmcpye.am.external.util.LogOnceLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import javax.annotation.PostConstruct;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.*;

import static java.io.File.separator;

public class DefaultLocationManager extends LogOnceLogger implements LocationManager {

    private final Logger log = LoggerFactory.getLogger(DefaultLocationManager.class);

    private static final String DEFAULT_AM_SYSTEM_HOME = "/opt/d2fsam";

    private static final String DEFAULT_ENV_VAR = "AM_HOME";

    private static final String DEFAULT_SYS_PROP = "AM.home";

    private static final String DEFAULT_CTX_VAR = "AM-home";

    private String externalDir;

    private String environmentVariable;

    private String systemProperty;

    private String contextVariable;

    public DefaultLocationManager(String environmentVariable, String systemProperty, String contextVariable) {
        this.environmentVariable = environmentVariable;
        this.systemProperty = systemProperty;
        this.contextVariable = contextVariable;
    }

    public static DefaultLocationManager getDefault() {
        return new DefaultLocationManager(DEFAULT_ENV_VAR, DEFAULT_SYS_PROP, DEFAULT_CTX_VAR);
    }

    // -------------------------------------------------------------------------
    // Init
    // -------------------------------------------------------------------------

    @PostConstruct
    public void init() {
        // check the most specific to the least specific

        // 1- context variable
        externalDir = getPathFromContext();

        // 2- system property
        if (externalDir == null)
            externalDir = getPathFromSysProperty();

        // 3- env variable
        if (externalDir == null)
            externalDir = getPathFromEnvVariable();

        // 4- default value
        if (externalDir == null)
            externalDir = getPathDefault();
    }

    // -------------------------------------------------------------------------
    // LocationManager implementation
    // -------------------------------------------------------------------------
    private String getPathFromContext() {
        String path = null;
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            path = (String) envCtx.lookup(this.contextVariable);
        } catch (NamingException e) {
            log(log, Level.INFO, "Context variable " + contextVariable + " not set");
        }
        if (path != null) {
            log(log, Level.INFO, "Context variable " + contextVariable + " points to " + path);

            if (directoryIsValid(new File(path))) {
                return path;
            }
        }
        return null;
    }

    private String getPathFromSysProperty() {
        String path = System.getProperty(systemProperty);
        if (path != null) {
            log(log, Level.INFO, "System property " + systemProperty + " points to " + path);

            if (directoryIsValid(new File(path))) {
                return path;
            }
        } else {
            log(log, Level.INFO, "System property " + systemProperty + " not set");
        }
        return null;
    }

    private String getPathFromEnvVariable() {
        String path = System.getenv(environmentVariable);
        if (path != null) {
            log(log, Level.INFO, "Environment variable " + environmentVariable + " points to " + path);

            if (directoryIsValid(new File(path))) {
                return path;
            }
        } else {
            log(log, Level.INFO, "Environment variable " + environmentVariable + " not set");
        }
        return null;
    }

    private String getPathDefault() {
        String path = DEFAULT_AM_SYSTEM_HOME;
        if (directoryIsValid(new File(path))) {
            log(log, Level.INFO, "Home directory set to " + DEFAULT_AM_SYSTEM_HOME);
            return path;
        } else {
            log(log, Level.ERROR, "No Home directory set, and " + DEFAULT_AM_SYSTEM_HOME + " is not a directory");
            return null;
        }
    }
    // -------------------------------------------------------------------------
    // InputStream
    // -------------------------------------------------------------------------

    @Override
    public InputStream getInputStream(String fileName)
        throws LocationManagerException {
        return getInputStream(fileName, new String[0]);
    }

    @Override
    public InputStream getInputStream(String fileName, String... directories)
        throws LocationManagerException {
        File file = getFileForReading(fileName, directories);

        try {
            return new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException ex) {
            throw new LocationManagerException("Could not find file", ex);
        }
    }

    // -------------------------------------------------------------------------
    // File for reading
    // -------------------------------------------------------------------------

    @Override
    public File getFileForReading(String fileName)
        throws LocationManagerException {
        return getFileForReading(fileName, new String[0]);
    }

    @Override
    public File getFileForReading(String fileName, String... directories)
        throws LocationManagerException {
        File directory = buildDirectory(directories);

        File file = new File(directory, fileName);

        if (!canReadFile(file)) {
            throw new LocationManagerException("File " + file.getAbsolutePath() + " cannot be read");
        }

        return file;
    }

    // -------------------------------------------------------------------------
    // OutputStream
    // -------------------------------------------------------------------------

    @Override
    public OutputStream getOutputStream(String fileName)
        throws LocationManagerException {
        return getOutputStream(fileName, new String[0]);
    }

    @Override
    public OutputStream getOutputStream(String fileName, String... directories)
        throws LocationManagerException {
        File file = getFileForWriting(fileName, directories);

        try {
            return new BufferedOutputStream(new FileOutputStream(file));
        } catch (FileNotFoundException ex) {
            throw new LocationManagerException("Could not find file", ex);
        }
    }

    // -------------------------------------------------------------------------
    // File for writing
    // -------------------------------------------------------------------------

    @Override
    public File getFileForWriting(String fileName)
        throws LocationManagerException {
        return getFileForWriting(fileName, new String[0]);
    }

    @Override
    public File getFileForWriting(String fileName, String... directories)
        throws LocationManagerException {
        File directory = buildDirectory(directories);

        if (!directoryIsValid(directory)) {
            throw new LocationManagerException("Directory " + directory.getAbsolutePath() + " cannot be created");
        }

        return new File(directory, fileName);
    }

    @Override
    public File buildDirectory(String... directories)
        throws LocationManagerException {
        if (externalDir == null) {
            throw new LocationManagerException("External directory not set");
        }

        StringBuilder directoryPath = new StringBuilder(externalDir + separator);

        for (String dir : directories) {
            directoryPath.append(dir).append(separator);
        }

        return new File(directoryPath.toString());
    }

    // -------------------------------------------------------------------------
    // External directory and environment variable
    // -------------------------------------------------------------------------

    @Override
    public File getExternalDirectory()
        throws LocationManagerException {
        if (externalDir == null) {
            throw new LocationManagerException("External directory not set");
        }

        return new File(externalDir);
    }

    public void setExternalDir(String externalDir) {
        this.externalDir = externalDir;
    }

    @Override
    public String getExternalDirectoryPath()
        throws LocationManagerException {
        if (externalDir == null) {
            throw new LocationManagerException("External directory not set");
        }

        return externalDir;
    }

    @Override
    public boolean externalDirectorySet() {
        return externalDir != null;
    }

    @Override
    public String getEnvironmentVariable() {
        return environmentVariable;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Tests whether the file exists and can be read by the application.
     */
    private boolean canReadFile(File file) {
        if (!file.exists()) {
            log(log, Level.INFO, "File " + file.getAbsolutePath() + " does not exist");
            return false;
        }

        if (!file.canRead()) {
            log(log, Level.INFO, "File " + file.getAbsolutePath() + " cannot be read");

            return false;
        }

        return true;
    }

    /**
     * Tests whether the directory is writable by the application if the
     * directory exists. Tries to create the directory including necessary
     * parent directories if the directory does not exists, and tests whether
     * the directory construction was successful and not prevented by a
     * SecurityManager in any way.
     */
    private boolean directoryIsValid(File directory) {
        if (directory.exists()) {
            if (!directory.canWrite()) {
                log(log, Level.INFO, "Directory " + directory.getAbsolutePath() + " is not writeable");
                return false;
            }
        } else {
            try {
                if (!directory.mkdirs()) {
                    log(log, Level.INFO, "Directory " + directory.getAbsolutePath() + " cannot be created");
                    return false;
                }
            } catch (SecurityException ex) {
                log(log, Level.INFO, "Directory " + directory.getAbsolutePath() + " cannot be accessed");
                return false;
            }
        }

        return true;
    }
}
