package repositories;

import io.ebeaninternal.api.SpiExpression;
import io.ebeaninternal.api.SpiExpressionRequest;
import io.ebeaninternal.server.el.ElPropertyValue;
import io.ebeaninternal.server.expression.AbstractValueExpression;
import io.ebeaninternal.server.expression.DocQueryContext;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by Corey Caplan on 12/13/17.
 * <p></p>
 * Used to create <i>date_trunc('YEAR', prop_name) = date_trunc('YEAR'</i>  comparative expressions
 */
public class TruncateDateExpression extends AbstractValueExpression {

    public enum TruncateValue {
        DAY, WEEK, MONTH, QUARTER, YEAR
    }

    private final TruncateValue truncateValue;

    /**
     * Construct with property name and potential named parameter.
     *
     * @param propName      The property name of the class
     * @param bindValue     The value that will be bound as a parameter
     * @param truncateValue The date value to which the date value should be truncated
     */
    public TruncateDateExpression(String propName, Object bindValue, TruncateValue truncateValue) {
        super(propName, bindValue);
        this.truncateValue = truncateValue;
    }


    @Override
    public void writeDocQuery(DocQueryContext context) throws IOException {
        // DO NOTHING - We don't support elastic search
    }

    @Override
    public void queryPlanHash(StringBuilder builder) {
        String truncateValue = this.truncateValue.toString().toLowerCase();
        builder.append("DATE_TRUNC(\'").append(truncateValue).append("\', ").append(propName).append(")")
                .append(" = ")
                .append("DATE_TRUNC(\'").append(truncateValue).append("\', ").append(strValue()).append(")");
    }

    @Override
    public int queryBindHash() {
        return truncateValue.hashCode() + strValue().hashCode();
    }

    @Override
    public boolean isSameByBind(SpiExpression other) {
        if (!(other instanceof TruncateDateExpression)) {
            return false;
        } else {
            TruncateDateExpression that = (TruncateDateExpression) other;
            return this.strValue().equals(that.strValue());
        }
    }

    @Override
    public void addSql(SpiExpressionRequest request) {
        String pname = propName;
        ElPropertyValue prop = getElProp(request);
        if (prop != null && prop.isDbEncrypted()) {
            pname = prop.getBeanProperty().getDecryptProperty(propName);
        }

        String truncateValue = this.truncateValue.toString().toLowerCase();
        request.append("DATE_TRUNC(\'").append(truncateValue).append("\', ").append(pname).append(")")
                .append(" = ")
                .append("DATE_TRUNC(\'").append(truncateValue).append("\', ? :: TIMESTAMP)");
    }

    @Override
    public void addBindValues(SpiExpressionRequest request) {
        String bindValue = new SimpleDateFormat("MM-dd-yyyy HH:MM:ss")
                .format(super.bindValue);

        request.addBindValue(bindValue);
    }
}
