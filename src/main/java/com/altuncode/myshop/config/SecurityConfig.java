package com.altuncode.myshop.config;

import com.altuncode.myshop.services.PersonDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    private final PersonDetailsService personDetailsService;


    @Autowired
    public SecurityConfig(@Qualifier("PersonDetailsService") PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl(); // Enables session tracking
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }




    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(session -> {
                    session
                            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                            .maximumSessions(10)
                            .maxSessionsPreventsLogin(true)
                            .expiredUrl("/authentication");
                    session
                            .invalidSessionUrl("/")
                            .sessionFixation(sessionFixation -> sessionFixation.newSession());
                }).csrf(withDefaults())
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )
                .formLogin(login -> login
                        .loginPage()
                        .loginProcessingUrl()
                        .defaultSuccessUrl()
                        .failureUrl()
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessUrl("/authentication")
                )
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
//                        .contentSecurityPolicy(csp -> csp
//                                .policyDirectives("default-src 'self'; script-src 'self'; object-src 'none'; base-uri 'self'; form-action 'self'; style-src 'self'; img-src 'self'; font-src 'self'; frame-ancestors 'self'; block-all-mixed-content; upgrade-insecure-requests; report-to csp-endpoint")
//                        )
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000)
                        )
                )
        ;

        return http.build();
    }


}