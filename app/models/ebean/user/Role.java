package models.ebean.user;

import io.ebean.annotation.DbEnumType;
import io.ebean.annotation.DbEnumValue;

/**
 *
 */
public enum Role {

    ADMINISTRATOR, LEGAL_CONTRACTOR, FISCAL_MANAGER;

    @DbEnumValue(storage = DbEnumType.VARCHAR)
    public String getRawText() {
        return this.toString().toLowerCase();
    }

    public String getUiText() {
        String[] parts = this.toString().split("_+");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            builder.append(parts[i].substring(0, 1).toUpperCase())
                    .append(parts[i].substring(1).toLowerCase());
            if (i < parts.length - 1) {
                builder.append(" ");
            }
        }
        return builder.toString();
    }

    public int getPriority() {
        if (this == ADMINISTRATOR) {
            return 1000;
        } else if (this == LEGAL_CONTRACTOR) {
            return 200;
        } else if (this == FISCAL_MANAGER) {
            return 100;
        } else {
            throw new RuntimeException("Invalid THIS, found: " + this.toString());
        }
    }

    public UserRoleModel convertToUserRoleModel() {
        UserRoleModel model = new UserRoleModel();
        model.setRoleType(this.getRawText());
        model.setRoleDescription(this.getUiText());
        model.setRolePriority(this.getPriority());
        return model;
    }

}
