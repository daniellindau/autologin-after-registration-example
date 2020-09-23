package io.curity.identityserver.plugin.autologin.anonymous

import io.curity.identityserver.plugin.autologin.descriptor.AutologinConfig
import org.hibernate.validator.constraints.NotEmpty
import se.curity.identityserver.sdk.authentication.AnonymousRequestHandler
import se.curity.identityserver.sdk.service.AutoLoginManager
import se.curity.identityserver.sdk.service.ExceptionFactory
import se.curity.identityserver.sdk.service.authentication.AuthenticatorInformationProvider
import se.curity.identityserver.sdk.web.Request
import se.curity.identityserver.sdk.web.Response

class NonceReceivingRequestHandler(config: AutologinConfig) :
        AnonymousRequestHandler<NonceRequestModel>
{
    private val autoLoginManager: AutoLoginManager = config.autoLoginManager
    private val pathHelper: AuthenticatorInformationProvider = config.pathHelper
    private val exceptionFactory: ExceptionFactory = config.exceptionFactory

    override fun preProcess(request: Request, p1: Response): NonceRequestModel = NonceRequestModel(request)

    override fun get(model: NonceRequestModel, p1: Response): Void
    {
        autoLoginManager.enableAutoLoginNonce(model.nonce)
        throw exceptionFactory.redirectException(pathHelper.fullyQualifiedAuthenticationUri.toString());
    }

    override fun post(model: NonceRequestModel, p1: Response?): Void
    {
        autoLoginManager.enableAutoLoginNonce(model.nonce)
        throw exceptionFactory.redirectException(pathHelper.fullyQualifiedAuthenticationUri.toString());
    }
}

class NonceRequestModel(request: Request)
{
    @NotEmpty
    val nonce: String = request.getParameterValueOrError("nonce")
}
