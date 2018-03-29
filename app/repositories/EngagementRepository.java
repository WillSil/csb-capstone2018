package repositories;

import io.ebean.Ebean;
import io.ebean.ExpressionList;
import io.ebean.Query;
import models.AutomateCsbPagedList;
import models.ebean.CsbBaseModel;
import models.ebean.business.Business;
import models.ebean.business.Engagement;
import models.ebean.status.ChildStatus;
import models.search.EngagementSearch;
import play.Logger;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * Created by Corey Caplan on 11/15/17.
 */
public class EngagementRepository extends BaseRepository {

    private static final Logger.ALogger logger = Logger.of(EngagementRepository.class);

    private final BusinessRepository businessRepository;
    private final ChildStatusRepository childStatusRepository;

    @Inject
    public EngagementRepository(BusinessRepository businessRepository, ChildStatusRepository childStatusRepository) {
        this.businessRepository = businessRepository;
        this.childStatusRepository = childStatusRepository;
    }

    /**
     * @param search      The criteria used by the repository to filter and sort the engagement search.
     * @param currentPage A 0-based index of the page into the result set.
     * @return A paged-list of engagements, which contains the current position in the paged list and the total # of rows
     */
    public CompletionStage<AutomateCsbPagedList<Engagement>> getEngagements(EngagementSearch search, int currentPage) {
        return getPagedListFromTransaction(() -> {

            ExpressionList<Engagement> expressions = Ebean.find(Engagement.class)
                    .fetch("business")
                    .fetch("currentStateType")
                    .where();

            if (search.getDateFilter() != null) {
                expressions = bindDateFilterToExpressions(expressions, search);
            }

            if (search.getPhaseFilter() != null) {
                expressions = expressions.and()
                        .eq("currentStateType", search.getPhaseFilter())
                        .endAnd();
            }

            SearchTransaction<Engagement> transaction = new SearchTransaction<>(expressions);

            expressions = transaction.bindValueToPredicate(search.getBusinessNamePredicate(), "business.businessName", search.isOrFilter())
                    .bindValueToPredicate(search.getBusinessId(), "business.businessId", search.isOrFilter())
                    .bindValueToPredicate(search.getEngagementNamePredicate(), "engagementName", search.isOrFilter())
                    .getExpressionList();

            String sortByColumn = convertSortByPredicateToColumn(search.getSortByCriteria());
            Query<Engagement> query;
            if (search.isAscending()) {
                query = expressions.order().asc(sortByColumn);
            } else {
                query = expressions.order().desc(sortByColumn);
            }

            return query.setFirstRow(currentPage * ROW_COUNT)
                    .setMaxRows(ROW_COUNT)
                    .findPagedList();
        });
    }

    public CompletionStage<Engagement> getEngagementDetailsById(int engagementId) {
        return executeTransaction(() -> Ebean.find(Engagement.class)
                .fetch("business")
                .fetch("currentStateType")
                .where()
                .idEq(engagementId)
                .findUnique());
    }

    public CompletionStage<Engagement> createEngagement(int businessId, String engagementName, String engagementNotes,
                                                        Date dateStarted, Date dateEnded, Date dateImplemented) {

        if (businessRepository.getBusinessDetailsById(businessId) == null) {
            // return error
            logger.info("Invalid business ID, found: {}", businessId);
            return null;
        }

        return executeTransaction(() -> {
            Engagement engagement = new Engagement();
            engagement.setEngagementName(engagementName);

            Business business = new Business();
            business.setBusinessId(businessId);

            ChildStatus status = childStatusRepository.getFirstChildStatus().toCompletableFuture().join();
            if (status == null) {
                return null;
            }
            engagement.setCurrentStateType(status);

            engagement.setBusiness(business);

            if (engagementNotes != null && engagementNotes.trim().isEmpty()) {
                engagement.setEngagementNotes(null);
            } else {
                engagement.setEngagementNotes(engagementNotes);
            }

            engagement.setDateStarted(dateStarted);
            engagement.setDateEnded(dateEnded);
            engagement.setDateStatusLastUpdated(dateStarted);
            engagement.setDateImplemented(dateImplemented);
            engagement.setActive(true);

            Ebean.save(engagement);
            return engagement;
        });
    }

    public CompletionStage<Engagement> updateEngagement(int engagementId, String engagementName, Boolean isActive,
                                                        String engagementNotes, Date dateStarted, Date dateEnded,
                                                        Date dateImplemented, Date dateStatusLastUpdated) {
        return executeTransaction(() -> {
            Optional<Engagement> engagementOptional = Ebean.find(Engagement.class)
                    .where()
                    .idEq(engagementId)
                    .findOneOrEmpty();

            engagementOptional.ifPresent(engagement -> {

                if (engagementName != null) {
                    engagement.setEngagementName(engagementName);
                }

                if (engagementNotes != null) {
                    if (engagementNotes.trim().isEmpty()) {
                        engagement.setEngagementNotes(null);
                    } else {
                        engagement.setEngagementNotes(engagementNotes);
                    }
                }

                if (isActive != null) {
                    engagement.setActive(isActive);
                }

                if (dateStarted != null) {
                    engagement.setDateStarted(dateStarted);
                }

                if (dateEnded != null) {
                    engagement.setDateEnded(dateEnded);
                }

                if (dateImplemented != null) {
                    engagement.setDateImplemented(dateImplemented);
                }

                if (dateStatusLastUpdated != null) {
                    engagement.setDateStatusLastUpdated(dateStatusLastUpdated);
                }

                Ebean.save(engagement);
            });

            return engagementOptional.orElse(null);
        });
    }

    public CompletionStage<Engagement> updateEngagementStatus(int engagementId, ChildStatus status) {
        return executeTransaction(() -> {
            Optional<Engagement> engagementOptional = Ebean.find(Engagement.class)
                    .where()
                    .idEq(engagementId)
                    .findOneOrEmpty();

            engagementOptional.ifPresent(engagement -> {
                engagement.setCurrentStateType(status);
                engagement.setDateStatusLastUpdated(new Date());
                Ebean.save(engagement);
            });

            return engagementOptional.orElse(null);

        });
    }

    public CompletionStage<Engagement> deleteEngagement(int engagementId) {
        return executeTransaction(() -> {
            Optional<Engagement> engagementOptional = Ebean.find(Engagement.class)
                    .where().idEq(engagementId)
                    .findOneOrEmpty();

            engagementOptional.ifPresent(Ebean::delete);

            return engagementOptional.orElse(null);
        });
    }

    // Mark - Private Methods

    private <T extends CsbBaseModel> ExpressionList<T> bindDateFilterToExpressions(ExpressionList<T> expressions, EngagementSearch search) {
        Calendar nextYear = Calendar.getInstance();
        nextYear.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) + 1);

        TruncateDateExpression thisYearExpression =
                new TruncateDateExpression("dateImplemented", new Date(), TruncateDateExpression.TruncateValue.YEAR);

        TruncateDateExpression nextYearExpression =
                new TruncateDateExpression("dateImplemented", new Date(nextYear.getTimeInMillis()), TruncateDateExpression.TruncateValue.YEAR);

        switch (search.getDateFilter()) {
            case EngagementSearch.DateFilter.PAST_FULL:
                return expressions.and()
                        .eq("isActive", false)
                        .eq("currentStateType.statusChildType", "phase_8")
                        .endAnd();
            case EngagementSearch.DateFilter.PAST_INCOMPLETE:
                return expressions.and()
                        .eq("isActive", false)
                        .ne("currentStateType.statusChildType", "phase_8")
                        .endAnd();
            case EngagementSearch.DateFilter.CURRENT:
                return expressions.and()
                        .eq("isActive", true)
                        .add(thisYearExpression)
                        .endAnd();
            case EngagementSearch.DateFilter.FUTURE:
                return expressions.and()
                        .eq("isActive", true)
                        .add(nextYearExpression)
                        .endAnd();
            default:
                logger.error("Error: Developer forgot something... ", new IllegalStateException());
                return expressions;
        }
    }

    private String convertSortByPredicateToColumn(String sortByCriteria) {
        if (EngagementSearch.SortByCriteria.BUSINESS_NAME.equalsIgnoreCase(sortByCriteria)) {
            return "business.businessName";
        } else if (EngagementSearch.SortByCriteria.ENGAGEMENT_NAME.equalsIgnoreCase(sortByCriteria)) {
            return "engagementName";
        } else if (EngagementSearch.SortByCriteria.STATUS_LAST_UPDATED.equalsIgnoreCase(sortByCriteria)) {
            return "dateStatusLastUpdated";
        } else {
            logger.error("Invalid sort by criteria, found: {}", sortByCriteria);
            logger.error("Error: ", new IllegalStateException());
            return "business.businessName";
        }
    }

}