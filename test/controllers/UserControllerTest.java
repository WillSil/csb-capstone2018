package controllers;

import clients.FirebaseAuthenticationMock;
import com.fasterxml.jackson.databind.JsonNode;
import global.BaseTestApplicationForTesting;
import io.ebean.Ebean;
import models.ebean.invite.InvitedUser;
import models.ebean.user.Role;
import models.ebean.user.User;
import models.ebean.user.UserRoleModel;
import models.ebean.user.query.QUser;
import org.junit.Before;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import java.util.Date;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static play.test.Helpers.*;

/**
 * Created by Corey Caplan on 11/2/17.
 */
public class UserControllerTest extends BaseTestApplicationForTesting {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testBadLogin() throws Exception {
        JsonNode node = Json.newObject()
                .put(User.KEY_TOKEN, FirebaseAuthenticationMock.getInvalidToken());

        Http.RequestBuilder builder = createRequestBuilderWithNoAuth(routes.UserController.login())
                .bodyJson(node);

        Result result = route(app, builder);
        assertEquals(401, result.status());
    }

    @Test
    public void testBadLoginServerError() throws Exception {
        JsonNode node = Json.newObject()
                .put(User.KEY_TOKEN, FirebaseAuthenticationMock.getMalformedToken());

        Http.RequestBuilder builder = createRequestBuilderWithNoAuth(routes.UserController.login())
                .bodyJson(node);

        Result result = route(app, builder);
        assertEquals(500, result.status());
    }

    @Test
    public void testGoodCreateAccount() throws Exception {
        setupInvitedUser();

        JsonNode node = Json.newObject()
                .put(User.KEY_TOKEN, FirebaseAuthenticationMock.getOkayToken());

        Http.RequestBuilder builder = createRequestBuilderWithNoAuth(routes.UserController.login())
                .bodyJson(node);

        Result result = route(app, builder);
        assertEquals(200, result.status());
    }

    @Test
    public void testGoodLogin() throws Exception {
        setupGoodAccount();

        JsonNode node = Json.newObject()
                .put(User.KEY_TOKEN, FirebaseAuthenticationMock.getOkayToken());

        Http.RequestBuilder builder = createRequestBuilderWithNoAuth(routes.UserController.login())
                .bodyJson(node);

        Result result = route(app, builder);
        assertEquals(200, result.status());
    }

    private void setupGoodAccount() {
        User user = User.getDummy(Role.ADMINISTRATOR);
        Optional.ofNullable(Ebean.find(User.class).where()
                .eq(QUser.alias().userId.toString(), user.getUserId())
                .findOne())
                .orElseGet(() -> {
                    Ebean.insert(user);
                    return user;
                });
    }

    private void setupInvitedUser() {
        User dummy = User.getDummy(Role.ADMINISTRATOR);
        InvitedUser invitedUser = new InvitedUser();
        invitedUser.setEmail(dummy.getEmail());
        invitedUser.setInviteDate(new Date());

        UserRoleModel userRoleModel = new UserRoleModel();
        userRoleModel.setRoleType(Role.ADMINISTRATOR.getRawText());
        userRoleModel.setRoleDescription(Role.ADMINISTRATOR.getUiText());
        userRoleModel.setRolePriority(Role.ADMINISTRATOR.getPriority());
        invitedUser.setUserRole(userRoleModel);

        Ebean.save(invitedUser);
    }

}