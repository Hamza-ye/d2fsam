package org.nmcpye.am.test.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.nmcpye.am.BaseSpringTest;
import org.nmcpye.am.IntegrationTest;
import org.nmcpye.am.config.IntegrationTestConfig;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {IntegrationTestConfig.class})
@IntegrationTest
@ActiveProfiles(profiles = {"testdev", "testprod", "test-postgres"})
public abstract class IntegrationTestBase extends BaseSpringTest {
    @BeforeEach
    public final void before()
        throws Exception {
        bindSession();

        integrationTestBefore();
    }

    @AfterEach
    public final void after()
        throws Exception {
        nonTransactionalAfter();
    }
}
