/*
 *  Copyright 2020 Curity AB
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.curity.identityserver.plugin.autologin.descriptor

import io.curity.identityserver.plugin.autologin.anonymous.NonceReceivingRequestHandler
import io.curity.identityserver.plugin.autologin.authentication.AutologinAuthenticatorRequestHandler
import io.curity.identityserver.plugin.autologin.registration.AutologinRegistrationRequestHandler
import se.curity.identityserver.sdk.config.Configuration
import se.curity.identityserver.sdk.plugin.descriptor.AuthenticatorPluginDescriptor
import se.curity.identityserver.sdk.service.AutoLoginManager
import se.curity.identityserver.sdk.service.ExceptionFactory
import se.curity.identityserver.sdk.service.authentication.AuthenticatorInformationProvider

class AutologinAfterRegistrationAuthenticatorPluginDescriptor : AuthenticatorPluginDescriptor<AutologinConfig>
{
    override fun getAuthenticationRequestHandlerTypes() =
            mapOf("index" to AutologinAuthenticatorRequestHandler::class.java)

    override fun getAnonymousRequestHandlerTypes() =
            mapOf("index" to NonceReceivingRequestHandler::class.java)

    override fun getRegistrationRequestHandlerTypes() =
            mapOf("index" to AutologinRegistrationRequestHandler::class.java)

    override fun getConfigurationType() = AutologinConfig::class.java

    override fun getPluginImplementationType(): String = "autologin"
}

interface AutologinConfig : Configuration
{
    val autoLoginManager: AutoLoginManager
    val pathHelper: AuthenticatorInformationProvider
    val exceptionFactory: ExceptionFactory
}
