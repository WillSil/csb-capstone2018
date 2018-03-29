package models.search;

import models.ebean.status.ChildStatus;
import play.Logger;
import utilities.ParameterConstants;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by Corey Caplan on 11/15/17.
 */
public class EngagementSearch {

    private static final Logger.ALogger logger = Logger.of(EngagementSearch.class);

    public interface SortByCriteria {

        String BUSINESS_NAME = ParameterConstants.BUSINESS_NAME;
        String ENGAGEMENT_NAME = ParameterConstants.ENGAGEMENT_NAME;
        String STATUS_LAST_UPDATED = ParameterConstants.ENGAGEMENT_DATE_STATUS_LAST_UPDATED;

        static String parse(String rawSortByCriteria) {
            if (rawSortByCriteria == null) {
                return ENGAGEMENT_NAME;
            }

            if (BUSINESS_NAME.equalsIgnoreCase(rawSortByCriteria)) {
                return BUSINESS_NAME;
            } else if (ENGAGEMENT_NAME.equalsIgnoreCase(rawSortByCriteria)) {
                return ENGAGEMENT_NAME;
            } else if (STATUS_LAST_UPDATED.equalsIgnoreCase(rawSortByCriteria)) {
                return STATUS_LAST_UPDATED;
            } else {
                logger.info("Invalid SortByCriteria, found: {}", rawSortByCriteria);
                return ENGAGEMENT_NAME;
            }
        }

    }

    public interface DateFilter {

        String ALL = "all";
        String PAST_FULL = "past_full";
        String PAST_INCOMPLETE = "past_incomplete";
        String CURRENT = "current";
        String FUTURE = "future";

        static String parse(String rawSortByCriteria) {
            if (rawSortByCriteria == null) {
                return null;
            }

            if (ALL.equalsIgnoreCase(rawSortByCriteria)) {
                return null;
            } else if (PAST_FULL.equalsIgnoreCase(rawSortByCriteria)) {
                return PAST_FULL;
            } else if (PAST_INCOMPLETE.equalsIgnoreCase(rawSortByCriteria)) {
                return PAST_INCOMPLETE;
            } else if (CURRENT.equalsIgnoreCase(rawSortByCriteria)) {
                return CURRENT;
            } else if (FUTURE.equalsIgnoreCase(rawSortByCriteria)) {
                return FUTURE;
            } else {
                logger.info("Invalid DateFilter, found: {}", rawSortByCriteria);
                return null;
            }
        }

    }

    private final String engagementNamePredicate;
    private final String businessNamePredicate;
    private final String dateFilter;
    private final ChildStatus phaseFilter;
    private final boolean isOrFilter;
    private final String sortByCriteria;
    private final int businessId;
    private final boolean isAscending;

    /**
     * @param businessId              The business ID or -1 if empty.
     * @param engagementNamePredicate The name of the engagement or null for any.
     * @param businessNamePredicate   The name of the business or null for any.
     * @param dateFilter              The date filter for the engagement or null for any.
     * @param isOrFilter              True to look for any of the preceding engagements or false to apply an "AND" to
     *                                all of them.
     * @param sortByCriteria          The criteria by which to sort the engagements.
     * @param isAscending             True to sort ascending on the sorting criteria or false for descending.
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public EngagementSearch(int businessId, String engagementNamePredicate, String businessNamePredicate,
                            String dateFilter, ChildStatus phaseFilter, boolean isOrFilter, String sortByCriteria, boolean isAscending) {
        this.businessId = businessId;
        this.engagementNamePredicate = engagementNamePredicate;
        this.businessNamePredicate = businessNamePredicate;
        this.dateFilter = dateFilter;
        this.phaseFilter = phaseFilter;
        this.isOrFilter = isOrFilter;
        this.sortByCriteria = sortByCriteria;
        this.isAscending = isAscending;
    }

    public int getBusinessId() {
        return businessId;
    }

    public String getEngagementNamePredicate() {
        return engagementNamePredicate;
    }

    public String getBusinessNamePredicate() {
        return businessNamePredicate;
    }

    public String getDateFilter() {
        return dateFilter;
    }

    public ChildStatus getPhaseFilter() {
        return phaseFilter;
    }

    public boolean isOrFilter() {
        return isOrFilter;
    }

    public String getSortByCriteria() {
        return sortByCriteria;
    }

    public boolean isAscending() {
        return isAscending;
    }

}
