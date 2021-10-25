package se.company.services.database;

import java.util.Date;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import se.company.services.models.ServiceRecord;
import se.company.services.utils.Logger;


public class ServiceDB {

    private DBConnector database;

    private static final String GET_ALL_SERVICES = "SELECT * FROM services ORDER BY created ASC;";
    private static final String GET_SERVICE_BY_URL = "SELECT * FROM services WHERE url = ?;";
    private static final String ADD_SERVICE = "INSERT INTO services (name, url, created) VALUES (?, ?, ?);";
    private static final String UPDATE_SERVICE_STATUS = "UPDATE services SET status = ?, updated = ? WHERE url = ?;";
    private static final String UPDATE_SERVICE_NAME = "UPDATE services SET name = ?, updated = ? WHERE url = ?;";
    private static final String DELETE_SERVICE = "DELETE FROM services WHERE url = ?;";


    public ServiceDB(DBConnector database) {
        this.database = database;
    }


    public Future<ResultSet> getAllServices() {
        return database.query(GET_ALL_SERVICES);
    }


    public Future<ResultSet> getServiceByUrl(String url) {
        var params = new JsonArray().add(url);
        return database.query(GET_SERVICE_BY_URL, params);
    }


    public Future<Boolean> addService(ServiceRecord serviceRecord) {
        var params = new JsonArray().add(serviceRecord.name()).add(serviceRecord.url()).add(new Date().toString());
        Promise<Boolean> finalResult = Promise.promise();

        var result = database.query(ADD_SERVICE, params).onComplete(r -> {
            if (r.succeeded()) {
                Logger.debug(ServiceDB.class.toString() + " addService succeeded!");
                finalResult.complete(Boolean.TRUE);
            } else {
                Logger.debug(ServiceDB.class.toString() + " addService failed!");
                finalResult.fail("Add service failed!");
            }
        });
        return finalResult.future();
    }


    public Future<Boolean> updateServiceName(ServiceRecord serviceRecord) {
        var params = new JsonArray().add(serviceRecord.name()).add(new Date().toString()).add(serviceRecord.url());
        Promise<Boolean> finalResult = Promise.promise();

        var result = database.query(UPDATE_SERVICE_NAME, params).onComplete(r -> {
            if (r.succeeded()) {
                Logger.debug(ServiceDB.class.toString() + " updateServiceName succeeded!");
                finalResult.complete(Boolean.TRUE);
            } else {
                Logger.debug(ServiceDB.class.toString() + " updateServiceName failed!");
                finalResult.fail("Update service's name failed.");
            }
        });
        return finalResult.future();
    }


    public Future<Boolean> updateServiceStatus(ServiceRecord serviceRecord) {
        var params = new JsonArray().add(serviceRecord.status()).add(new Date().toString()).add(serviceRecord.url());
        database.query(UPDATE_SERVICE_STATUS, params);
        return Future.succeededFuture(true);
    }


    public Future<Boolean> deleteService(ServiceRecord serviceRecord) {
        var params = new JsonArray().add(serviceRecord.url());
        database.query(DELETE_SERVICE, params);
        return Future.succeededFuture(true);
    }
}
