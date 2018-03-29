package repositories;

import io.ebean.ExpressionList;
import models.ebean.CsbBaseModel;
import play.Logger;

/**
 * Created by Corey Caplan on 11/27/17.
 */
class SearchTransaction<T extends CsbBaseModel> {

    private static final Logger.ALogger logger = Logger.of(SearchTransaction.class);

    private ExpressionList<T> expressionList;

    SearchTransaction(ExpressionList<T> expressionList) {
        this.expressionList = expressionList;
    }

    /**
     * Binds the predicateValue to a column if the value is not null. This predicate is equivalent to the following in
     * a SQL where clause:
     * <code>OR/AND lower(<i>column_name</i>) like lower(<i>predicate_value</i>)</code>
     *
     * @param predicateValue The predicate's value to be bound to this search.
     * @param columnName     The column name that binds to this value
     * @param isOrExpression True if this is used in a SQL <i>OR</i>, false for a SQL <i>AND</i>. This parameter is
     *                       ignored if isLastPredicate is true OR the predicate value is null.
     * @return "this" for method chaining
     */
    SearchTransaction<T> bindValueToPredicate(String predicateValue, String columnName, boolean isOrExpression) {
        if (predicateValue != null) {
            expressionList = addJunction(isOrExpression).ilike(columnName, "%" + predicateValue + "%");
        }
        return this;
    }

    /**
     * Binds the predicateValue to a column if the value is not null. This predicate is equivalent to the following in
     * a SQL where clause:
     * <code>OR/AND lower(<i>column_name</i>) like lower(<i>predicate_value</i>)</code>
     *
     * @param predicateValue The predicate's value to be bound to this search.
     * @param columnName     The column name that binds to this value
     * @param isOrExpression True if this is used in a SQL <i>OR</i>, false for a SQL <i>AND</i>. This parameter is
     *                       ignored if isLastPredicate is true OR the predicate value is null.
     * @return "this" for method chaining
     */
    @SuppressWarnings("SameParameterValue")
    SearchTransaction<T> bindValueToPredicate(int predicateValue, String columnName, boolean isOrExpression) {
        if (predicateValue != -1) {
            expressionList = addJunction(isOrExpression).eq(columnName, predicateValue);
        }
        return this;
    }

    /**
     * @return The expression list, after it has been modified by making multiple calls to
     * {@link #bindValueToPredicate(int, String, boolean)}.
     */
    ExpressionList<T> getExpressionList() {
        return expressionList;
    }

// MARK - Private Methods

    private ExpressionList<T> addJunction(boolean isOrExpression) {
        return isOrExpression ? expressionList.or() : expressionList.and();
    }

}
