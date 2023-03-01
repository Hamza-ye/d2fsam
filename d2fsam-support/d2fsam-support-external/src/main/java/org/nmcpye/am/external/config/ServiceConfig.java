package org.nmcpye.am.external.config;

import org.nmcpye.am.external.conf.ConfigurationPropertyFactoryBean;
import org.nmcpye.am.external.location.DefaultLocationManager;
import org.nmcpye.am.external.location.LocationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.nmcpye.am.external.conf.ConfigurationKey.*;

/**
 * Created by Hamza on 18/11/2021.
 */
@Configuration("externalServiceConfig")
@EnableAsync
@EnableScheduling
public class ServiceConfig {

    @Bean
    public LocationManager locationManager() {
        return DefaultLocationManager.getDefault();
    }

    @Bean("maxAttempts")
    public ConfigurationPropertyFactoryBean maxAttempts() {
        return new ConfigurationPropertyFactoryBean(META_DATA_SYNC_RETRY);
    }

    @Bean("initialInterval")
    public ConfigurationPropertyFactoryBean initialInterval() {
        return new ConfigurationPropertyFactoryBean(META_DATA_SYNC_RETRY_TIME_FREQUENCY_MILLISEC);
    }

    @Bean("sessionTimeout")
    public ConfigurationPropertyFactoryBean sessionTimeout() {
        return new ConfigurationPropertyFactoryBean(SYSTEM_SESSION_TIMEOUT);
    }
}
