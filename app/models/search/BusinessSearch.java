package models.search;

import play.Logger;

/**
 * Created by Corey Caplan on 10/17/17.
 */
public class BusinessSearch {

    private static final Logger.ALogger logger = Logger.of(BusinessSearch.class);

    private final String businessNamePredicate;
    private final String businessEmailPredicate;
    private final String clientNamePredicate;
    private final String clientEmailPredicate;
    private final String businessIndustryPredicate;
    private final boolean isOrFilter;
    private final String sortByPredicate;
    private final boolean isAscending;

    public BusinessSearch(String businessNamePredicate, String businessEmailPredicate, String clientNamePredicate,
                          String clientEmailPredicate, String businessIndustryPredicate, boolean isOrFilter,
                          String sortByPredicate, boolean isAscending) {
        this.businessEmailPredicate = businessEmailPredicate;
        this.businessNamePredicate = businessNamePredicate;
        this.clientNamePredicate = clientNamePredicate;
        this.clientEmailPredicate = clientEmailPredicate;
        this.businessIndustryPredicate = businessIndustryPredicate;
        this.isOrFilter = isOrFilter;
        this.sortByPredicate = sortByPredicate;
        this.isAscending = isAscending;
    }

    public String getBusinessNamePredicate() {
        return businessNamePredicate;
    }

    public String getBusinessEmailPredicate() {
        return businessEmailPredicate;
    }

    public String getClientNamePredicate() {
        return clientNamePredicate;
    }

    public String getClientEmailPredicate() {
        return clientEmailPredicate;
    }

    public String getBusinessIndustryPredicate() {
        return businessIndustryPredicate;
    }

    public boolean isOrFilter() {
        return isOrFilter;
    }

    public String getSortByPredicate() {
        return sortByPredicate;
    }

    public boolean isAscending() {
        return isAscending;
    }

    public interface SortByPredicate {
        String BUSINESS_NAME = "businessName";

        static String parse(String rawSortByPredicate) {
            if (rawSortByPredicate == null) {
                return BUSINESS_NAME;
            }

            if (BUSINESS_NAME.equalsIgnoreCase(rawSortByPredicate)) {
                return BUSINESS_NAME;
            } else {
                logger.info("Invalid sort by predicate, found: {}", rawSortByPredicate);
                return BUSINESS_NAME;
            }
        }
    }

}
