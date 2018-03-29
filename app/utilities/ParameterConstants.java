package utilities;

/**
 * Created by Corey Caplan on 11/15/17.
 */
public interface ParameterConstants {

    // Businesses
    String BUSINESS_ID = "businessId";
    String BUSINESS_NAME = "businessName";
    String BUSINESS_EMAIL = "businessEmail";
    String BUSINESS_INDUSTRY = "businessIndustry";

    // Engagements
    String ENGAGEMENT_ID = "engagementId";
    String ENGAGEMENT_NAME = "engagementName";
    String ENGAGEMENT_IS_ACTIVE = "isActive";
    String ENGAGEMENT_NOTES = "engagementNotes";
    String ENGAGEMENT_DATE_STARTED = "dateStarted";
    String ENGAGEMENT_DATE_ENDED = "dateEnded";
    String ENGAGEMENT_DATE_IMPLEMENTED = "dateImplemented";
    String ENGAGEMENT_DATE_STATUS_LAST_UPDATED = "dateStatusLastUpdated";
    String ENGAGEMENT_DATE_FILTER = "engagementDateFilter";

    // Clients
    String CLIENT_NAME = "clientName";
    String CLIENT_EMAIL = "clientEmail";

    // Child Status
    String CHILD_STATUS_TYPE = "child_status_type";

    /*
     ******************** Descriptions ********************
     */

    String DESCRIPTION_DATE = "The date, formatted as the milliseconds since the Unix Epoch";

    // Engagement Specific Searching
    String DESCRIPTION_DATE_FILTER = "Filtering by past/present/future";

    // Paging & Searching
    String DESCRIPTION_ASCENDING = "True to have the data be returned in ascending order, false for descending order";
    String DESCRIPTION_IS_OR_FILTER = "True to apply OR predicates to the filter options or false to apply AND predicates.";
    String DESCRIPTION_PAGE = "A 0-index page number for retrieving the data. Used for retrieving paged results.";
    String DESCRIPTION_SORT_BY = "The value on which the list should be sorted when returned back from the server.";

}
