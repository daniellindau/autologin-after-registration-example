package io.curity.identityserver.plugin.autologin.registration

import io.curity.identityserver.plugin.autologin.descriptor.AutologinConfig
import org.hibernate.validator.constraints.NotEmpty
import se.curity.identityserver.sdk.attribute.AccountAttributes
import se.curity.identityserver.sdk.authentication.RegistrationRequestHandler
import se.curity.identityserver.sdk.authentication.RegistrationResult
import se.curity.identityserver.sdk.errors.ErrorCode
import se.curity.identityserver.sdk.service.AutoLoginManager
import se.curity.identityserver.sdk.service.ExceptionFactory
import se.curity.identityserver.sdk.web.Request
import se.curity.identityserver.sdk.web.Response
import se.curity.identityserver.sdk.web.Response.ResponseModelScope.NOT_FAILURE
import se.curity.identityserver.sdk.web.ResponseModel.templateResponseModel
import java.util.Optional
import java.util.UUID

class AutologinRegistrationRequestHandler(config : AutologinConfig)
    : RegistrationRequestHandler<RegistrationRequestModel>
{
    private val autoLoginManager = config.autoLoginManager
    private val exceptionFactory = config.exceptionFactory
    private val pathHelper= config.pathHelper

    override fun preProcess(request: Request, response: Response): RegistrationRequestModel =
            if (request.isGetRequest)
            {
                GetRegistrationRequestModel(request)
            }
            else
            {
                PostRegistrationRequestModel(request)
            }

    override fun get(registrationRequestModel: RegistrationRequestModel, response: Response):
            Optional<RegistrationResult>
    {
        response.setResponseModel(templateResponseModel(emptyMap(), "templates/authenticator/autologin/resources/register/get"), NOT_FAILURE)
        return Optional.empty()
    }

    override fun post(model: RegistrationRequestModel, response: Response): Optional<RegistrationResult>
    {
        model as PostRegistrationRequestModel
        val confirmData = autoLoginManager.prepareAutoLoginNonce(AccountAttributes.of(UUID.randomUUID().toString(),
                model.userName)).orElseThrow {
            exceptionFactory.internalServerException(ErrorCode.GENERIC_ERROR, "Could not create autologin nonce")
        }

        // Create the autologin link containing the session key, and return it to the template
        val anonUri = pathHelper.fullyQualifiedAnonymousUri
        val loginLink = anonUri.toString() + "?nonce=" + confirmData.tokenKey
        response.setResponseModel(templateResponseModel(mapOf("_autoLoginLink" to loginLink),
                "templates/authenticator/autologin/resources/register/post"), NOT_FAILURE)
        return Optional.empty()


    }
}

sealed class RegistrationRequestModel(request: Request)

class PostRegistrationRequestModel(request: Request) : RegistrationRequestModel(request)
{
    @NotEmpty
    val userName: String = request.getFormParameterValueOrError("username")
}

// No validation required
class GetRegistrationRequestModel(request: Request) : RegistrationRequestModel(request)
