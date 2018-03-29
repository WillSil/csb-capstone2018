package actions;

import controllers.BaseController;
import play.mvc.Http;
import clients.AuthenticatorClient;

import javax.inject.Inject;

/**
 * Created by Corey Caplan on 11/2/17.
 */
public class LegalContractorAuthenticator extends BaseAuthenticator {

    @Inject
    public LegalContractorAuthenticator(AuthenticatorClient authenticatorClient) {
        super(authenticatorClient);
    }

    @Override
    AuthenticatorClient.AuthenticatorResult getResultFromRequestHeaders(Http.Headers headers) {
        return getAuthenticatorClient()
                .isLegalContractorAuthenticated(headers.get(BaseController.AUTHORIZATION).orElse(null));
    }

}
