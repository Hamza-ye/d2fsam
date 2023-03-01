//package org.nmcpye.am.config;
//
//import org.nmcpye.am.external.conf.AmConfigurationProvider;
//import org.nmcpye.am.external.conf.ConfigurationKey;
//import org.springdoc.core.GroupedOpenApi;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import tech.jhipster.config.JHipsterConstants;
//import tech.jhipster.config.apidoc.customizer.JHipsterOpenApiCustomizer;
//
//@Configuration
//@Profile(JHipsterConstants.SPRING_PROFILE_API_DOCS)
//public class OpenApiConfiguration {
//
//    public static final String API_FIRST_PACKAGE = "org.nmcpye.am.web.api";
//
//    @Bean
//    @ConditionalOnMissingBean(name = "apiFirstGroupedOpenAPI")
//    public GroupedOpenApi apiFirstGroupedOpenAPI(
//        JHipsterOpenApiCustomizer jhipsterOpenApiCustomizer,
//        AmConfigurationProvider amConfig) {
//        return GroupedOpenApi
//            .builder()
//            .group("openapi")
//            .addOpenApiCustomiser(jhipsterOpenApiCustomizer)
//            .packagesToScan(API_FIRST_PACKAGE)
//            .pathsToMatch(amConfig.getProperty(ConfigurationKey.API_DOCS_INCLUDE_PATTERN))
//            .build();
//    }
//
////    @Bean
////    @ConditionalOnMissingBean(name = "apiFirstGroupedOpenAPI")
////    public GroupedOpenApi apiFirstGroupedOpenAPI(
////        JHipsterOpenApiCustomizer jhipsterOpenApiCustomizer,
////        JHipsterProperties jHipsterProperties
////    ) {
////        JHipsterProperties.ApiDocs properties = jHipsterProperties.getApiDocs();
////        return GroupedOpenApi
////            .builder()
////            .group("openapi")
////            .addOpenApiCustomiser(jhipsterOpenApiCustomizer)
////            .packagesToScan(API_FIRST_PACKAGE)
////            .pathsToMatch(properties.getDefaultIncludePattern())
////            .build();
////    }
//}
