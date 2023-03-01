//package org.nmcpye.am.webapi.servlet;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
//
//@Order(10)
//@Configuration
//public class AMWebApiWebAppInitializer { // implements WebApplicationInitializer {
//
//    private final Logger log = LoggerFactory.getLogger(AMWebApiWebAppInitializer.class);
//
//    @Bean
//    public FilterRegistrationBean registerOpenEntityManagerInViewFilterBean() {
//        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//        OpenEntityManagerInViewFilter filter = new OpenEntityManagerInViewFilter();
//        registrationBean.setFilter(filter);
//        registrationBean.setOrder(5);
//        return registrationBean;
//    }
//    //    @Override
//    //    public void onStartup(ServletContext context) {
//    //        FilterRegistration.Dynamic openSessionInViewFilter = context.addFilter("openSessionInViewFilter",
//    //            OpenSessionInViewFilter.class);
//    //        openSessionInViewFilter.setInitParameter("sessionFactoryBeanName", "sessionFactory");
//    //        openSessionInViewFilter.addMappingForUrlPatterns(null, false, "/*");
//    //        openSessionInViewFilter.addMappingForServletNames(null, false, "dispatcher");
//    //
//    ////        FilterRegistration.Dynamic characterEncodingFilter = context.addFilter( "characterEncodingFilter",
//    ////            CharacterEncodingFilter.class );
//    ////        characterEncodingFilter.setInitParameter( "encoding", "UTF-8" );
//    ////        characterEncodingFilter.setInitParameter( "forceEncoding", "true" );
//    ////        characterEncodingFilter.addMappingForUrlPatterns( null, false, "/*" );
//    ////        characterEncodingFilter.addMappingForServletNames( null, false, "dispatcher" );
//    //
//    //        context.addFilter("RequestIdentifierFilter", new DelegatingFilterProxy("requestIdentifierFilter"))
//    //            .addMappingForUrlPatterns(null, true, "/*");
//    //
//    //        context.addFilter("AppOverrideFilter", new DelegatingFilterProxy("appOverrideFilter"))
//    //            .addMappingForUrlPatterns(null, true, "/*");
//    //
//    //        context.addListener(new StartupListener());
//    //    }
//}
