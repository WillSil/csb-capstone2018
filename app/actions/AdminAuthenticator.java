package actions;

import controllers.BaseController;
import play.mvc.Http;
import clients.AuthenticatorClient;

import javax.inject.Inject;

/**
 * Created by Corey Caplan on 10/25/17.
 */
public class AdminAuthenticator extends BaseAuthenticator {

    @Inject
    public AdminAuthenticator(AuthenticatorClient authenticatorClient) {
        super(authenticatorClient);
    }

    @Override
    AuthenticatorClient.AuthenticatorResult getResultFromRequestHeaders(Http.Headers headers) {
        return getAuthenticatorClient()
                .isAdminAuthenticated(headers.get(BaseController.AUTHORIZATION).orElse(null));
    }

}
