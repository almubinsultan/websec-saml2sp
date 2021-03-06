/*
 * Copyright 2014 Vincenzo De Notaris
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package de.chludwig.websec.saml2sp.springconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.List;

/**
 * Adaption of Spring Boot's default MVC configuration.
 */
@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

    @Autowired
    CurrentUserHandlerMethodArgumentResolver currentUserHandlerMethodArgumentResolver;

    /**
     * Register the resolver for the {@link de.chludwig.websec.saml2sp.stereotypes.CurrentUser @CurrentUser}
     * handler method argument annotation.
     *
     * @param argumentResolvers
     *         the resolver collection the current user resolver is added to
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(currentUserHandlerMethodArgumentResolver);
    }

    /**
     * Servlet context initializer that sets a custom session cookie name.
     *
     * Otherwise, saml2sp and WSO2's Identity Server overwrite each other's session cookie if they are
     * both serving localhost (on different ports). This breaks the SSO flow.
     *
     * @return a context initializer that sets the session cookie name to {@code "SAML2SPSESSIONID"}.
     */
    @Bean
    public ServletContextInitializer servletContextInitializer() {
        return new ServletContextInitializer() {

            @Override
            public void onStartup(ServletContext servletContext) throws ServletException {
                servletContext.getSessionCookieConfig().setName("SAML2SPSESSIONID");
            }
        };
    }
}
