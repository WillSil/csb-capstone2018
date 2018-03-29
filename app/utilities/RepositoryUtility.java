package utilities;

import io.ebean.typequery.TQProperty;

/**
 * Created by Corey Caplan on 11/9/17.
 */
public final class RepositoryUtility {

    // No instance
    private RepositoryUtility() {
    }

    public static String appendQualifier(TQProperty property, String typeSafeProperty) {
        return property.toString() + "." + typeSafeProperty;
    }

    public static String appendQualifier(TQProperty property1, TQProperty property2) {
        return property1.toString() + "." + property2.toString();
    }

}
