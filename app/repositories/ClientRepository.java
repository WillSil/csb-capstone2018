package repositories;

import io.ebean.Ebean;
import io.ebean.OrderBy;
import io.ebean.Query;
import models.AutomateCsbPagedList;
import models.ebean.business.Business;
import models.ebean.business.Client;
import models.ebean.business.query.QClient;
import play.Logger;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * Created by Corey Caplan on 11/1/17.
 */
public class ClientRepository extends BaseRepository {

    private static final Logger.ALogger logger = Logger.of(EngagementRepository.class);
    private final BusinessRepository businessRepository;

    @Inject
    public ClientRepository(BusinessRepository businessRepository) {
        this.businessRepository = businessRepository;

    }

    public CompletionStage<AutomateCsbPagedList<Client>> getAllClientsForBusiness(long businessId, int currentPage,
                                                                                  boolean isAscending) {
        return getPagedListFromTransaction(() -> {
            OrderBy<Client> orderBy = Ebean.createQuery(Client.class)
                    .where()
                    .eq("business.businessId", businessId)
                    .order();

            Query<Client> clientQuery;
            if (isAscending) {
                clientQuery = orderBy.asc(QClient.alias().clientName.toString());
            } else {
                clientQuery = orderBy.desc(QClient.alias().clientName.toString());
            }

            return clientQuery
                    .setMaxRows(ROW_COUNT)
                    .setFirstRow(ROW_COUNT * currentPage)
                    .findPagedList();
        });
    }

    public CompletionStage<Client> createClient(int businessId, String clientName, String clientEmail, String clientPhone,
                                                String clientNotes) {
        return executeTransaction(() -> {
            Business business = businessRepository.getBusinessDetailsById(businessId)
                    .toCompletableFuture()
                    .join();

            if (business == null) {
                logger.info("Invalid business ID, found: {}", businessId);
                return null;
            }

            Client client = new Client();
            client.setClientName(clientName);
            client.setClientEmail(clientEmail);
            client.setBusiness(business);

            if (clientPhone != null && clientPhone.trim().isEmpty()) {
                client.setClientPhone(null);
            } else {
                client.setClientPhone(clientPhone);
            }

            if (clientNotes != null && clientNotes.trim().isEmpty()) {
                client.setClientNotes(null);
            } else {
                client.setClientNotes(clientNotes);
            }

            Ebean.save(client);

            return client;
        });
    }


    public CompletionStage<Client> updateClient(int businessId, int clientId, String clientName, String clientEmail, String clientPhone,
                                                String clientNotes) {
        return executeTransaction(() -> {
            Optional<Client> clientOptional = Ebean.find(Client.class)
                    .where().idEq(clientId)
                    .findOneOrEmpty();

            clientOptional.ifPresent(client -> {
                if (clientName != null) {
                    client.setClientName(clientName);
                }
                if (clientEmail != null) {
                    client.setClientEmail(clientEmail);
                }

                if (clientPhone != null) {
                    client.setClientPhone(clientPhone);
                }

                if (clientNotes != null) {
                    client.setClientNotes(clientNotes);
                }

                Ebean.save(client);
            });

            return clientOptional.orElse(null);

        });
    }


    public CompletionStage<Client> deleteClient(int businessId, int clientId) {

        return executeTransaction(() -> {
            Optional<Client> engagementOptional = Ebean.find(Client.class)
                    .where().idEq(clientId)
                    .findOneOrEmpty();

            engagementOptional.ifPresent(Ebean::delete);

            return engagementOptional.orElse(null);
        });
    }


}
