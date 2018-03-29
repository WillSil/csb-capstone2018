package repositories;

import io.ebean.Ebean;
import io.ebean.PagedList;
import models.AutomateCsbPagedList;
import models.ebean.CsbBaseModel;
import play.Logger;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Created by Corey Caplan on 10/24/17.
 */
public class BaseRepository {

    static final int ROW_COUNT = 25;

    interface TransactionCallable<T> {

        T execute();
    }

    interface PagedListCallable<T extends CsbBaseModel> {

        PagedList<T> execute();
    }


    private static final Logger.ALogger logger = Logger.of(BaseRepository.class);

    @Inject
    public BaseRepository() {
    }

    /**
     * @param transactionCallable A function that takes no parameters and returns T result
     * @param <T>                 The type of return value.
     * @return A completion stage with any value returned
     */
    <T> CompletionStage<T> executeTransaction(TransactionCallable<T> transactionCallable) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Ebean.beginTransaction();
                T t = transactionCallable.execute();
                if (t != null) {
                    Ebean.commitTransaction();
                    return t;
                } else {
                    Ebean.rollbackTransaction();
                    return null;
                }
            } finally {
                Ebean.endTransaction();
            }
        }).exceptionally(throwable -> {
            logger.error("Error occurred in transaction: ", throwable);
            return null;
        });
    }

    /**
     * @param transactionCallable A function that takes no parameters and returns T result
     * @param <T>                 The type of return value.
     * @return A completion stage with any value returned
     */
    <T extends CsbBaseModel> CompletionStage<AutomateCsbPagedList<T>> getPagedListFromTransaction(PagedListCallable<T> transactionCallable) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Ebean.beginTransaction();
                PagedList<T> p = transactionCallable.execute();
                if (p != null) {
                    AutomateCsbPagedList<T> automateCsbPagedList = new AutomateCsbPagedList<>(p.getPageSize(),
                            p.getPageIndex(), p.getTotalCount(), p.getList());
                    Ebean.commitTransaction();
                    return automateCsbPagedList;
                } else {
                    Ebean.rollbackTransaction();
                    return null;
                }
            } catch (Exception e) {
                Ebean.rollbackTransaction();
                logger.error("Error in transaction: {}", e);
                return null;
            } finally {
                Ebean.endTransaction();
            }
        }).exceptionally(throwable -> {
            logger.error("Error occurred in transaction: ", throwable);
            return null;
        });
    }

}
