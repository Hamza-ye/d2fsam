package org.nmcpye.am.test.integration;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.nmcpye.am.BaseSpringTest;
import org.nmcpye.am.IntegrationTest;
import org.nmcpye.am.config.IntegrationTestConfig;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(classes = {IntegrationTestConfig.class})
@IntegrationTest
@ActiveProfiles(profiles = {"testdev", "testprod", "test-postgres"})
@Transactional
@Slf4j
public abstract class TransactionalIntegrationTest extends BaseSpringTest {

    @BeforeEach
    public final void before()
        throws Exception {
        integrationTestBefore();
    }

    @AfterEach
    public final void after()
        throws Exception {
        clearSecurityContext();

        tearDownTest();

        try {
            dbmsManager.clearSession();
        } catch (Exception e) {
            log.info("Failed to clear hibernate session, reason:" + e.getMessage());
        }
    }
}
