package com.home.delivery.security;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.inject.Inject;

/**
 * Created by evgeny on 22.05.15.
 */
@Configuration
@EnableWebSecurity
public class AppSecurityConfig extends WebSecurityConfigurerAdapter {

    static final String DISPATCHER_ROLE = "DISPATCHER";
    static final String DRIVER_ROLE = "DRIVER";

    private static final ImmutableMap<String, String> ROLES_TO_PAGES = ImmutableMap.of(
            "ROLE_" + DISPATCHER_ROLE, "/deliveries",
            "ROLE_" + DRIVER_ROLE, "/driver"
    );

    private static final Log log = LogFactory.getLog(AppSecurityConfig.class);

    @Inject
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("dispatcher").password("dispatcher").roles(DISPATCHER_ROLE);
        auth.inMemoryAuthentication().withUser("driver1").password("driver1").roles(DRIVER_ROLE);
        auth.inMemoryAuthentication().withUser("driver2").password("driver2").roles(DRIVER_ROLE);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().
                authorizeRequests().
                antMatchers("/**").access("hasRole('ROLE_DISPATCHER')").
                antMatchers("/driver/**").access("hasRole('ROLE_DRIVER')").
                and().
                formLogin().
                successHandler(successHandler()).
                permitAll();
    }

    private AuthenticationSuccessHandler successHandler()
    {
        return (httpServletRequest, httpServletResponse, authentication) -> {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (ROLES_TO_PAGES.containsKey(authority.getAuthority())) {
                    httpServletResponse.sendRedirect(ROLES_TO_PAGES.get(authority.getAuthority()));
                    return;
                }
            }
        };
    }




}
