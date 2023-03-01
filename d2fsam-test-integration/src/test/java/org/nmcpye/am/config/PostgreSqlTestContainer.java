//package org.nmcpye.am.config;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.testcontainers.containers.JdbcDatabaseContainer;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.containers.output.Slf4jLogConsumer;
//import org.testcontainers.utility.DockerImageName;
//
//import java.util.Collections;
//
//public class PostgreSqlTestContainer implements SqlTestContainer {
//
//    private static final Logger log = LoggerFactory.getLogger(PostgreSqlTestContainer.class);
//
//    private static final DockerImageName POSTGIS_IMAGE_NAME = DockerImageName
//        .parse("postgis/postgis")
//        .asCompatibleSubstituteFor("postgres");
//
//    private PostgreSQLContainer<?> postgreSQLContainer;
//
//    //    @Override
//    public void destroy() {
//        if (null != postgreSQLContainer && postgreSQLContainer.isRunning()) {
//            postgreSQLContainer.stop();
//        }
//    }
//
//    //    @Override
//    public void afterPropertiesSet() {
//        if (null == postgreSQLContainer) {
//            postgreSQLContainer =
////                new PostgreSQLContainer<>("postgres:14.5")
//                new PostgreSQLContainer<>(POSTGIS_IMAGE_NAME)
//                    .withDatabaseName("d2fsamBack")
//                    .withTmpFs(Collections.singletonMap("/testtmpfs", "rw"))
//                    .withLogConsumer(new Slf4jLogConsumer(log))
//                    .withReuse(true);
//        }
//        if (!postgreSQLContainer.isRunning()) {
//            postgreSQLContainer.start();
//        }
//    }
//
//    @Override
//    public JdbcDatabaseContainer<?> getTestContainer() {
//        return postgreSQLContainer;
//    }
//}
