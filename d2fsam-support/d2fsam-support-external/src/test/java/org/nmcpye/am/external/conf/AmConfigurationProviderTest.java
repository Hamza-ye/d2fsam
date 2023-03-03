/*
 * Copyright (c) 2004-2022, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.nmcpye.am.external.conf;

import org.junit.jupiter.api.Test;
import org.nmcpye.am.external.location.DefaultLocationManager;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AmConfigurationProviderTest {

    @Test
    void isOn() {
        assertTrue(AmConfigurationProvider.isOn("on"));
        assertTrue(AmConfigurationProvider.isOn("ON"));
        assertTrue(AmConfigurationProvider.isOn("true"));
        assertTrue(AmConfigurationProvider.isOn("TRUE"));
        assertFalse(AmConfigurationProvider.isOn("off"));
        assertFalse(AmConfigurationProvider.isOn("OFF"));
        assertFalse(AmConfigurationProvider.isOn("false"));
        assertFalse(AmConfigurationProvider.isOn("FALSE"));
        assertFalse(AmConfigurationProvider.isOn(""));
        assertFalse(AmConfigurationProvider.isOn(null));
    }

    @Test
    void isEnabled() {
        System.setProperty("AM.home", "src/test/resources");
        DefaultLocationManager locationManager = DefaultLocationManager.getDefault();
        locationManager.init();
        DefaultAmConfigurationProvider configProvider = new DefaultAmConfigurationProvider(locationManager);
        configProvider.init();
        assertFalse(configProvider.isEnabled(ConfigurationKey.REDIS_ENABLED));
        assertFalse(configProvider.isEnabled(ConfigurationKey.MONITORING_API_ENABLED));
        assertTrue(configProvider.isEnabled(ConfigurationKey.DEBEZIUM_ENABLED));
        assertTrue(configProvider.isEnabled(ConfigurationKey.ENABLE_QUERY_LOGGING));
        assertFalse(configProvider.isEnabled(ConfigurationKey.METHOD_QUERY_LOGGING_ENABLED));
    }
}
