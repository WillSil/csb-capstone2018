package actions;

import controllers.BaseController;
import play.mvc.Http;
import clients.AuthenticatorClient;

import javax.inject.Inject;

/**
 * Created by Corey Caplan on 11/2/17.
 */
public class FiscalManagerAuthenticator extends BaseAuthenticator {

    @Inject
    public FiscalManagerAuthenticator(AuthenticatorClient authenticatorClient) {
        super(authenticatorClient);
    }

    @Override
    AuthenticatorClient.AuthenticatorResult getResultFromRequestHeaders(Http.Headers headers) {
        return getAuthenticatorClient()
                .isFiscalManagerAuthenticated(headers.get(BaseController.AUTHORIZATION).orElse(null));
    }

}
