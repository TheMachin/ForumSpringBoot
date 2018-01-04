package org.miage.m2.forum.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
@EnableAutoConfiguration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

    /**
     * Configuration of authentification in order to indicate the user and role table
     * @param auth
     * @throws Exception
     */
    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(dataSource)
                .usersByUsernameQuery("select email,mdp, enable from Utilisateur where email=?")
                .authoritiesByUsernameQuery("select user_email, role_name from users_roles where user_email=?");
    }


    /**
     * Configure l'accès des pages en fonction du role de l'utilisateur (internaute, admin...)
     * @param http
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/","/account/login**","/account/signup**").permitAll()
                .antMatchers("/administration/**").access("hasRole('ADMIN')")
                .and()
                    .formLogin()
                        .loginPage("/account/login")
                        .permitAll()
                        .usernameParameter("email").passwordParameter("password")
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/account/login?logout");
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
        /*auth.inMemoryAuthentication()
                .withUser("user")
                .password("password")
                .roles("USER")
                .and()
                .withUser("admin")
                .password("admin")
                .roles("ADMIN");*/
    }

}
