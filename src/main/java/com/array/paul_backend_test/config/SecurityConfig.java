package com.array.paul_backend_test.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity security) throws Exception
    {
        /*security.authorizeRequests()
            .antMatchers("#/account/login*").permitAll()*/
        security.httpBasic().disable();
        //security.csrf().disable();
        security.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        /*security.authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .formLogin()
            .loginPage("/account/login").permitAll();*/

    }
}
