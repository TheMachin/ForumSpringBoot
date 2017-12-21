package org.miage.m2.forum;

import org.miage.m2.forum.config.SecurityConfig;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;


public class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {

    
    public SecurityWebApplicationInitializer() {
        super(SecurityConfig.class);
    }
}
