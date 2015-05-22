package com.home.delivery.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by evgeny on 22.05.15.
 */
@Configuration
@EnableWebSecurity
public class AppSecurityConfig extends WebSecurityConfigurerAdapter {

    static final String DISPATCHER_ROLE = "DISPATCHER";
    static final String DRIVER_ROLE = "DRIVER";

    private static final Log log = LogFactory.getLog(AppSecurityConfig.class);

    @Inject
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("dispatcher").password("dispatcher").roles(DISPATCHER_ROLE);
        auth.inMemoryAuthentication().withUser("driver1").password("driver1").roles(DRIVER_ROLE);
        auth.inMemoryAuthentication().withUser("driver2").password("driver2").roles(DRIVER_ROLE);
//        auth.inMemoryAuthentication().withUser("superadmin").password("superadmin").roles("SUPERADMIN");
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
        return new AuthenticationSuccessHandler()
        {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest httpServletRequest,
                                                HttpServletResponse httpServletResponse,
                                                Authentication authentication) throws IOException, ServletException {
                for (GrantedAuthority authority : authentication.getAuthorities()) {
                    if (("ROLE_" + DISPATCHER_ROLE).equals(authority.getAuthority())) {
                        httpServletResponse.sendRedirect("/deliveries");
                    } else if (("ROLE_" + DRIVER_ROLE).equals(authority.getAuthority())) {
                        httpServletResponse.sendRedirect("/driver");
                    }
                }
            }
        };
    }




}
