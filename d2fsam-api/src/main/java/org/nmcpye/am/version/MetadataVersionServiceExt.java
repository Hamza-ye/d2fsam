package org.nmcpye.am.version;

import org.nmcpye.am.common.enumeration.VersionType;

import java.time.Instant;
import java.util.Date;
import java.util.List;

/**
 * Service Interface for managing {@link MetadataVersion}.
 */
public interface MetadataVersionServiceExt {
    String METADATAVERSION_NAME_PREFIX = "Version_";

    /**
     * Adds the metadata version.
     *
     * @param version the metadata version object to add.
     * @return the identifier of the saved version object.
     */
    long addVersion(MetadataVersion version);

    /**
     * Updates the metadata version.
     *
     * @param version the metadata version to update.
     */
    void updateVersion(MetadataVersion version);

    /**
     * Updates the name of the metadata version with the given identifier and
     * name.
     *
     * @param id   the identifier.
     * @param name the name.
     */
    void updateVersionName(long id, String name);

    /**
     * @param version Version object to delete.
     */
    void deleteVersion(MetadataVersion version);

    /**
     * Gets the metadata version with the given identifier.
     *
     * @param id Key to lookup the value with.
     * @return Version that matched key, or null if there was no match.
     */
    MetadataVersion getVersionById(long id);

    /**
     * @return List of all version objects.
     */
    List<MetadataVersion> getAllVersions();

    /**
     * Gets the instance's current version
     *
     * @return the current version at which the instance is.
     */
    MetadataVersion getCurrentVersion();

    /**
     * @return initial MetadataVersion of the system
     */
    MetadataVersion getInitialVersion();

    /**
     * Gets all versions between two data ranges on the created date.
     *
     * @param startDate the start date.
     * @param endDate   the end date.
     * @return a list of metadata versions matching the date range.
     */
    List<MetadataVersion> getAllVersionsInBetween(Date startDate, Date endDate);

    /**
     * Returns the created date of the version given the version name
     *
     * @param versionName the version name.
     * @return the created date.
     */
    Instant getCreatedDate(String versionName);

    /**
     * Gets the metadata version with the given name.
     *
     * @param versionName the version name.
     * @return the metadata version.
     */
    MetadataVersion getVersionByName(String versionName);

    /**
     * Saves or creates a version given the version type identifier
     *
     * @param versionType the version type.
     * @return true if created
     */
    boolean saveVersion(VersionType versionType);

    /**
     * Gets the Version data - the actual JSON snapshot given the version name.
     *
     * @param versionName
     * @return JSON data for the version snapshot
     */
    String getVersionData(String versionName);

    /**
     * Creates an entry in the DataStore given the MetadataVersion details.
     *
     * @param versionName
     * @param versionSnapshot
     */
    void createMetadataVersionInDataStore(String versionName, String versionSnapshot);

    /**
     * Checks the integrity of metadata by checking hash code.
     *
     * @param version
     * @param versionSnapshot
     * @return true if the metadata passes the integrity check.
     */
    boolean isMetadataPassingIntegrity(MetadataVersion version, String versionSnapshot);

    /**
     * Deletes the entry in Data Store given the versionName
     *
     * @param nameSpaceKey the name space key.
     */
    void deleteMetadataVersionInDataStore(String nameSpaceKey);
}
