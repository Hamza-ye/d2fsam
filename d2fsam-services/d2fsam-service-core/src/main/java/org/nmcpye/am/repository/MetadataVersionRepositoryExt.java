package org.nmcpye.am.repository;

import org.nmcpye.am.common.GenericStore;
import org.nmcpye.am.version.MetadataVersion;

import java.util.Date;
import java.util.List;

public interface MetadataVersionRepositoryExt extends GenericStore<MetadataVersion> {
    String ID = MetadataVersionRepositoryExt.class.getName();

    /**
     * @param id Key to lookup.
     * @return MetadataVersion Value that matched key, or null if there was no
     * match.
     */
    MetadataVersion getVersionByKey(long id);

    /**
     * Get the version by name.
     *
     * @param versionName
     * @return MetadataVersion object matched by the name
     */
    MetadataVersion getVersionByName(String versionName);

    /**
     * Gets the current version in the system.
     *
     * @return MetadataVersion object which is the latest in the system
     */
    MetadataVersion getCurrentVersion();

    /**
     * Gets MetadataVersion 's based on start created and end created dates
     *
     * @param startDate
     * @param endDate
     * @return List of MetadataVersion objects lying in that range of dates
     */
    List<MetadataVersion> getAllVersionsInBetween(Date startDate, Date endDate);

    /**
     * @return Initial/First MetadataVersion of the system
     */
    MetadataVersion getInitialVersion();
}
