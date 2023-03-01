package org.nmcpye.am.startup;

import org.nmcpye.am.setting.SettingKey;
import org.nmcpye.am.setting.SystemSettingManager;
import org.nmcpye.am.system.startup.AbstractStartupRoutine;

import static com.google.common.base.Preconditions.checkNotNull;

public class SettingUpgrader
    extends AbstractStartupRoutine {
    private final SystemSettingManager manager;

    public SettingUpgrader(SystemSettingManager manager) {
        checkNotNull(manager);
        this.manager = manager;
    }

    @Override
    public void execute()
        throws Exception {
        String startModule = manager.getStringSetting(SettingKey.START_MODULE);

        if ("amSystem-web-dashboard-integration".equals(startModule)) {
            manager.saveSystemSetting(SettingKey.START_MODULE, SettingKey.START_MODULE.getDefaultValue());
        }
    }
}
