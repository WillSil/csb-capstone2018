package repositories;

import io.ebean.Ebean;
import models.ebean.status.ChildStatus;
import models.ebean.status.query.QChildStatus;
import play.Logger;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * Created by Corey Caplan on 11/15/17.
 */
public class ChildStatusRepository extends BaseRepository {

    private static final Logger.ALogger logger = Logger.of(ChildStatusRepository.class);

    @Inject
    public ChildStatusRepository() {
    }

    public CompletionStage<List<ChildStatus>> getAllChildStatuses() {
        return executeTransaction(() -> Ebean.find(ChildStatus.class).findList());
    }

    public CompletionStage<ChildStatus> getChildStatusFromType(String childStatusType) {
        return executeTransaction(() -> Ebean.find(ChildStatus.class)
                .where()
                .eq(QChildStatus.alias().statusChildType.toString(), childStatusType)
                .findOne());
    }

    public CompletionStage<ChildStatus> getFirstChildStatus() {
        return executeTransaction(() -> Ebean.find(ChildStatus.class)
                .where()
                .idEq("phase_1")
                .findOne());
    }

}
