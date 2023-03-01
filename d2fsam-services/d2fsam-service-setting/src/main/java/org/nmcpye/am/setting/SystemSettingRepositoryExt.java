package org.nmcpye.am.setting;

import org.nmcpye.am.common.GenericStore;

public interface SystemSettingRepositoryExt
    extends GenericStore<SystemSetting> {
    /**
     * Returns the {@link SystemSetting} with the given name.
     *
     * @param name the system setting name.
     * @return a system setting.
     */
    SystemSetting getByName(String name);
}

