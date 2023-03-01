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
package org.nmcpye.am.startup;

import lombok.extern.slf4j.Slf4j;
import org.nmcpye.am.configuration.Configuration;
import org.nmcpye.am.configuration.ConfigurationService;
import org.nmcpye.am.encryption.EncryptionStatus;
import org.nmcpye.am.external.conf.AmConfigurationProvider;
import org.nmcpye.am.system.startup.TransactionContextStartupRoutine;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

@Slf4j
public class ConfigurationPopulator
    extends TransactionContextStartupRoutine {
    private final ConfigurationService configurationService;

    private final AmConfigurationProvider amConfigurationProvider;

    public ConfigurationPopulator(ConfigurationService configurationService,
                                  AmConfigurationProvider amConfigurationProvider) {
        checkNotNull(configurationService);
        checkNotNull(amConfigurationProvider);

        this.configurationService = configurationService;
        this.amConfigurationProvider = amConfigurationProvider;
    }

    @Override
    public void executeInTransaction() {
        checkSecurityConfiguration();

        Configuration config = configurationService.getConfiguration();

        if (config != null && config.getSystemId() == null) {
            config.setSystemId(UUID.randomUUID().toString());
            configurationService.setConfiguration(config);
        }
    }

    private void checkSecurityConfiguration() {
        EncryptionStatus status = amConfigurationProvider.getEncryptionStatus();

        if (!status.isOk()) {
            log.warn("Encryption not configured: " + status.getKey());
        } else {
            log.info("Encryption is available");
        }
    }
}
