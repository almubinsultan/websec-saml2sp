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

package de.chludwig.websec.saml2sp.stereotypes;

import java.lang.annotation.*;

/**
 * Handler method parameter annotation that makes the request handler bind the current
 * {@link de.chludwig.websec.saml2sp.security.ApplicationUser user} to the argument.
 * <p/>
 * The implementation is provided by
 * {@link de.chludwig.websec.saml2sp.springconfig.CurrentUserHandlerMethodArgumentResolver}.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {
}
