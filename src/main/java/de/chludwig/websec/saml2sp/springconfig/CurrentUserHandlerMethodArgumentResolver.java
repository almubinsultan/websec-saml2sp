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

import de.chludwig.websec.saml2sp.security.*;
import de.chludwig.websec.saml2sp.stereotypes.CurrentUser;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Stateless resolver bean that converts a Spring Security
 * {@link SecurityContext#getAuthentication() authentication} into a corresponding
 * {@link ApplicationUser} and binds it to handler method arguments annotated with
 * {@link CurrentUser @CurrentUser}.
 */
@Component
public class CurrentUserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterAnnotation(CurrentUser.class) != null &&
                methodParameter.getParameterType().isAssignableFrom(ApplicationUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        if (this.supportsParameter(methodParameter)) {
            return getCurrentUser();
        }
        else {
            return WebArgumentResolver.UNRESOLVED;
        }
    }

    public static ApplicationUser getCurrentUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication auth = (securityContext == null) ? null : securityContext.getAuthentication();
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            return new AnonymousUser();
        }
        else {
            SAMLCredential samlCredential = getSamlCredential(auth);
            String userId = getUserId(auth, samlCredential);
            String userName = getUserName(auth, samlCredential);
            Set<RoleId> roles = getUserRoles(auth);
            AuthenticationStatus authenticationStatus = getAuthenticationStatus(samlCredential);
            return new LoggedInUser(userId, userName, roles, authenticationStatus);
        }
    }

    private static SAMLCredential getSamlCredential(Authentication auth) {
        Object detailsObject = auth.getDetails();
        if (detailsObject instanceof SamlUserDetails) {
            SamlUserDetails samlUserDetails = (SamlUserDetails) detailsObject;
            return samlUserDetails.getSamlCredential();
        }
        return null;
    }

    private static String getUserId(Authentication auth, SAMLCredential samlCredential) {
        return (samlCredential == null) ? auth.getName() : samlCredential.getNameID().getValue();
    }

    private static String getUserName(Authentication auth, SAMLCredential samlCredential) {
        return getUserId(auth, samlCredential);
    }

    private static Set<RoleId> getUserRoles(Authentication auth) {
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        Set<RoleId> roles = new HashSet<>(authorities.size());
        for (GrantedAuthority authority : authorities) {
            RoleId roleId = RoleId.valueById(authority.getAuthority());
            if (roleId != null) {
                roles.add(roleId);
            }
        }
        return roles;
    }

    private static AuthenticationStatus getAuthenticationStatus(SAMLCredential samlCredential) {
        return AuthenticationStatus.PASSWORD_AUTHENTICATED;
    }
}
