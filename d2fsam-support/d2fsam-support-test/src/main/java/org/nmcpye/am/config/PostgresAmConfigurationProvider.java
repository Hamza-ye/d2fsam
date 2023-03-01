package org.nmcpye.am.config;

public class PostgresAmConfigurationProvider extends TestConfigurationProvider {

    private static final String DEFAULT_CONFIGURATION_FILE_NAME = "postgresTestConfig.conf";

    public PostgresAmConfigurationProvider() {
        this.properties = getPropertiesFromFile(DEFAULT_CONFIGURATION_FILE_NAME);
    }
}
