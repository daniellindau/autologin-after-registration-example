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

package io.curity.identityserver.plugin.autologin.authentication

import io.curity.identityserver.plugin.autologin.descriptor.AutologinConfig
import org.hibernate.validator.constraints.NotEmpty
import se.curity.identityserver.sdk.attribute.Attribute
import se.curity.identityserver.sdk.attribute.Attributes
import se.curity.identityserver.sdk.attribute.AuthenticationAttributes
import se.curity.identityserver.sdk.attribute.ContextAttributes
import se.curity.identityserver.sdk.attribute.SubjectAttributes
import se.curity.identityserver.sdk.authentication.AuthenticationResult
import se.curity.identityserver.sdk.authentication.AuthenticatorRequestHandler
import se.curity.identityserver.sdk.errors.ErrorCode
import se.curity.identityserver.sdk.web.Request
import se.curity.identityserver.sdk.web.Response
import java.util.Date
import java.util.Optional


class AutologinAuthenticatorRequestHandler(config: AutologinConfig)
    : AuthenticatorRequestHandler<AutologinRequestModel>
{
    private val autoLoginManager = config.autoLoginManager
    private val exceptionFactory = config.exceptionFactory

    override fun preProcess(request: Request, response: Response): AutologinRequestModel? = if (request.isGetRequest)
    {
        AutologinRequestModel(request)
    }
    else
    {
        null
    }

    override fun post(requestModel: AutologinRequestModel?, response: Response): Optional<AuthenticationResult> =
            throw exceptionFactory.methodNotAllowed("Only authentication using registration nonce available")

    override fun get(requestModel: AutologinRequestModel, response: Response): Optional<AuthenticationResult>
    {
        val previousResult = autoLoginManager.autoLoginFromCurrentSession.orElseThrow {
            exceptionFactory.badRequestException(ErrorCode.ACCESS_DENIED, "No valid nonce in the sesssion")
        }
        return Optional.of(
                AuthenticationResult(
                        AuthenticationAttributes.of(SubjectAttributes.of(previousResult.subject, Attributes.empty()),
                                ContextAttributes.of(Attributes.of(Attribute.of("iat", Date().time))))))
    }
}

class AutologinRequestModel(request: Request)
{
    @NotEmpty
    val sessionKey: String? = request.getParameterValueOrError("login-nonce")
}

