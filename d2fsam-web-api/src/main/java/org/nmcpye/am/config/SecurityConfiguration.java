//package org.nmcpye.am.config;
//
//import org.nmcpye.am.external.conf.AmConfigurationProvider;
//import org.nmcpye.am.external.conf.ConfigurationKey;
//import org.nmcpye.am.security.AuthoritiesConstants;
//import org.nmcpye.am.security.jwt.jhip.JWTConfigurer;
//import org.nmcpye.am.security.jwt.jhip.TokenProvider;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
//import org.springframework.web.filter.CorsFilter;
//import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;
//
//@EnableWebSecurity
//// NMCP Moved to WebMvcConfig from DH
////@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
//@Import(SecurityProblemSupport.class)
//public class SecurityConfiguration {
//
////    private final JHipsterProperties jHipsterProperties;
//
//    private final AmConfigurationProvider amConfig;
//
//    private final TokenProvider tokenProvider;
//
//    private final CorsFilter corsFilter;
//    private final SecurityProblemSupport problemSupport;
//
//    public SecurityConfiguration(
//        TokenProvider tokenProvider,
//        CorsFilter corsFilter,
//        AmConfigurationProvider amConfig,
//        SecurityProblemSupport problemSupport) {
//        this.tokenProvider = tokenProvider;
//        this.corsFilter = corsFilter;
//        this.problemSupport = problemSupport;
//        this.amConfig = amConfig;
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        // @formatter:off
//        http
//            .csrf()
//            .disable()
//            .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
//            .exceptionHandling()
//            .authenticationEntryPoint(problemSupport)
//            .accessDeniedHandler(problemSupport)
//            .and()
//            .headers()
////            .contentSecurityPolicy(jHipsterProperties.getSecurity().getContentSecurityPolicy())
//            .contentSecurityPolicy(amConfig.getProperty(ConfigurationKey.SECURITY_CONTENT_SECURITY_POLICY))
//            .and()
//            .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
//            .and()
//            .permissionsPolicy().policy("camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()")
//            .and()
//            .frameOptions().sameOrigin()
//            .and()
//            .sessionManagement()
//            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//            .and()
//            .authorizeRequests()
//            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//            .antMatchers("/app/**/*.{js,html}").permitAll()
//            .antMatchers("/i18n/**").permitAll()
//            .antMatchers("/content/**").permitAll()
//            .antMatchers("/swagger-ui/**").permitAll()
//            .antMatchers("/test/**").permitAll()
//            .antMatchers("/api/authenticate").permitAll()
//            .antMatchers("/api/register").permitAll()
//            .antMatchers("/api/activate").permitAll()
//            .antMatchers("/api/account/reset-password/init").permitAll()
//            .antMatchers("/api/account/reset-password/finish").permitAll()
//            .antMatchers("/api/admin/**").hasAuthority(AuthoritiesConstants.ADMIN)
//            .antMatchers("/api/**").authenticated()
//            .antMatchers("/websocket/**").authenticated()
//            .antMatchers("/management/health").permitAll()
//            .antMatchers("/management/health/**").permitAll()
//            .antMatchers("/management/info").permitAll()
//            .antMatchers("/management/prometheus").permitAll()
//            .antMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN)
//            .and()
//            .httpBasic()
//            .and()
//            .apply(securityConfigurerAdapter());
//        return http.build();
//        // @formatter:on
//    }
//
//    private JWTConfigurer securityConfigurerAdapter() {
//        return new JWTConfigurer(tokenProvider);
//    }
//}