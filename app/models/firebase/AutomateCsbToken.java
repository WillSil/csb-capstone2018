package models.firebase;

/**
 * Created by Corey Caplan on 11/6/17.
 */
public class AutomateCsbToken {

    private final String googleUserId;
    private final String email;
    private final String name;

    public AutomateCsbToken(String googleUserId, String email, String name) {
        this.googleUserId = googleUserId;
        this.email = email;
        this.name = name;
    }

    public String getGoogleUserId() {
        return googleUserId + 'g';
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
