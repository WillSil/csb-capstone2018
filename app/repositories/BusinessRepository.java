package repositories;

import io.ebean.Ebean;
import io.ebean.ExpressionList;
import io.ebean.PagedList;
import io.ebean.Query;
import models.AutomateCsbPagedList;
import models.ebean.business.Business;
import models.ebean.business.query.QBusiness;
import models.search.BusinessSearch;
import play.Logger;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class BusinessRepository extends BaseRepository {

    private static final Logger.ALogger logger = Logger.of(BusinessRepository.class);

    @Inject
    public BusinessRepository() {
        super();
    }

    /**
     * @param search      The criteria used by the repository to filter and sort the search.
     * @param currentPage A 0-based index of the page into the result set.
     * @return A paged list of businesses, which contains the current position in the paged list and the total # of rows
     */
    public CompletionStage<AutomateCsbPagedList<Business>> getBusinesses(BusinessSearch search, int currentPage) {
        return getPagedListFromTransaction(() -> {

            ExpressionList<Business> expressions = Ebean.createQuery(Business.class).fetch("clientList").where();

            SearchTransaction<Business> transaction = new SearchTransaction<>(expressions);

            expressions = transaction.bindValueToPredicate(search.getBusinessNamePredicate(), "businessName", search.isOrFilter())
                    .bindValueToPredicate(search.getBusinessEmailPredicate(), "businessEmail", search.isOrFilter())
                    .bindValueToPredicate(search.getBusinessIndustryPredicate(), "businessIndustry", search.isOrFilter())
                    .bindValueToPredicate(search.getClientNamePredicate(), "clientList.clientName", search.isOrFilter())
                    .bindValueToPredicate(search.getClientEmailPredicate(), "clientList.clientEmail", search.isOrFilter())
                    .getExpressionList();

            String sortByColumn = convertSortByPredicateToColumn(search.getSortByPredicate());
            Query<Business> query;
            if (search.isAscending()) {
                query = expressions.order().asc(sortByColumn);
            } else {
                query = expressions.order().desc(sortByColumn);
            }

            logger.debug("First Row: {}", currentPage * ROW_COUNT);

            query = query.setFirstRow(currentPage * ROW_COUNT)
                    .setMaxRows(ROW_COUNT);

            return query.findPagedList();
        });
    }

    public CompletionStage<Business> getBusinessDetailsById(int businessId) {
        return executeTransaction(() -> Ebean.find(Business.class)
                .where()
                .eq(QBusiness.alias().businessId.toString(), businessId)
                .findOne());
    }

    public CompletionStage<Business> createBusiness(String businessName, String businessEmail, String businessPhoneNumber,
                                                    String businessAddress, String businessIndustry) {

        return executeTransaction(() -> {
            Business business = new Business();
            business.setBusinessName(businessName);
            business.setBusinessEmail(businessEmail);
            business.setBusinessPhoneNumber(businessPhoneNumber);
            business.setBusinessAddress(businessAddress);
            business.setBusinessIndustry(businessIndustry);

            Ebean.save(business);

            return business;
        });
    }

    public CompletionStage<Business> updateBusiness(int businessId, String businessName, String businessEmail, String businessPhoneNumber,
                                                    String businessAddress, String businessIndustry) {
        return executeTransaction(() -> {
            Optional<Business> businessOptional = Ebean.find(Business.class)
                    .where().idEq(businessId)
                    .findOneOrEmpty();

            businessOptional.ifPresent(business -> {
                business.setBusinessName(businessName);
                business.setBusinessEmail(businessEmail);
                business.setBusinessPhoneNumber(businessPhoneNumber);
                business.setBusinessAddress(businessAddress);
                business.setBusinessIndustry(businessIndustry);
                Ebean.save(business);
            });

            return businessOptional.orElse(null);

        });
    }

    public CompletionStage<Business> deleteBusiness(int businessId) {

        return executeTransaction(() -> {
            Optional<Business> businessOptional = Ebean.find(Business.class)
                    .where().idEq(businessId)
                    .findOneOrEmpty();

            businessOptional.ifPresent(Ebean::delete);

            return businessOptional.orElse(null);
        });
    }

    // MARK - Private Methods

    private String convertSortByPredicateToColumn(String sortByPredicate) {
        if (BusinessSearch.SortByPredicate.BUSINESS_NAME.equalsIgnoreCase(sortByPredicate)) {
            return QBusiness.alias().businessName.toString();
        } else {
            logger.error("Error converting sort by predicate to column: ", new IllegalStateException());
            return QBusiness.alias().businessName.toString();
        }
    }

}
