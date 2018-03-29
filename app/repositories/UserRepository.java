package repositories;

import io.ebean.Ebean;
import models.ebean.invite.InvitedUser;
import models.ebean.invite.query.QInvitedUser;
import models.ebean.user.Role;
import models.ebean.user.User;
import models.ebean.user.UserRole;
import models.ebean.user.query.QUser;
import models.ebean.user.query.QUserRole;
import utilities.RepositoryUtility;

import javax.inject.Inject;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static utilities.RepositoryUtility.appendQualifier;

/**
 * Created by Corey Caplan on 10/22/17.
 */
public class UserRepository extends BaseRepository {

    @Inject
    public UserRepository() {
    }

    /**
     * This method assumes the user already authenticated between Firebase and this server
     *
     * @param googleAccountId The user's ID according to Google
     * @param email           The user's email
     * @return A completion stage containing a user. The user may be null if the credentials don't match, or this
     * user's role is inactive.
     */
    public CompletionStage<User> loginIfUserExists(String googleAccountId, String email) {
        return executeTransaction(() -> {
            QUser qUser = QUser.alias();
            Optional<User> userOptional = Optional.ofNullable(
                    Ebean.createQuery(User.class)
                            .where()
                            .eq(qUser.googleAccountId.toString(), googleAccountId)
                            .eq(qUser.email.toString(), email)
                            .findOne()
            );

            return userOptional.filter(user -> user.getUserRole().isActive())
                    .orElse(null);
        });
    }

    /**
     * This method assumes the user already authenticated between Firebase and this server
     *
     * @param googleAccountId The user's ID according to Google
     * @param email           The user's email
     * @param name            The user's name
     * @return A completion stage containing the created user. The user may be null if the user has not been invited to
     * use this service
     */
    public CompletionStage<User> createAccountIfUserIsInvited(String googleAccountId, String email, String name) {
        return executeTransaction(() -> {
            Optional<InvitedUser> invitedUserOptional = Optional.ofNullable(
                    Ebean.createQuery(InvitedUser.class)
                            .where()
                            .eq(QInvitedUser.alias().email.toString(), email)
                            .findOne()
            );

            return invitedUserOptional.map(invitedUser -> {
                User user = new User();
                user.setName(name);
                user.setEmail(email);
                user.setGoogleAccountId(googleAccountId);
                user.insert(); // auto-generates the userId

                UserRole userRole = new UserRole();
                userRole.setUserRoleModel(invitedUser.getUserRole());
                userRole.setActive(true);
                userRole.setDateAdded(new Date());

                user.setUserRole(userRole);
                user.save();

                return user;
            }).orElse(null);
        });
    }


    public CompletionStage<Boolean> isAdminCreated() {
        return executeTransaction(() -> Ebean.createQuery(User.class)
                .where()
                .eq("userRole.userRoleModel.roleType", Role.ADMINISTRATOR.getRawText())
                .findOneOrEmpty()
                .isPresent());
    }

    public CompletionStage<User> getUserByEmail(String email) {
        return executeTransaction(() -> Ebean.createQuery(User.class)
                .where()
                .eq(QUser.alias().email.toString(), email)
                .findOne()
        );
    }

    public CompletionStage<User> getUserById(String userId) {
        return executeTransaction(() -> Ebean.createQuery(User.class)
                .where()
                .eq(QUser.alias().userId.toString(), userId)
                .findOne()
        );
    }

}
