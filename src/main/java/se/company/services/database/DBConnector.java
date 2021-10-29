package se.company.services.database;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import se.company.services.utils.Logger;


public class DBConnector {

    private final String DB_PATH = "poller.db";
    private final SQLClient client;

    public DBConnector(Vertx vertx) {
        JsonObject config = new JsonObject()
                .put("url", "jdbc:sqlite:" + DB_PATH)
                .put("driver_class", "org.sqlite.JDBC")
                .put("max_pool_size", 30);

        client = JDBCClient.createShared(vertx, config);
        Logger.debug(DBConnector.class.toString() + " DBConnector config:" + config);
    }

    public Future<ResultSet> query(String query) {
        return query(query, new JsonArray());
    }

    public Future<ResultSet> query(String query, JsonArray params) {
        if (query == null || query.isEmpty()) {
            return Future.failedFuture("Query is null or empty");
        }
        if (!query.endsWith(";")) {
            query = query + ";";
        }
        Logger.debug(("query: " + query + " params: " + params));

        Promise<ResultSet> queryResultFuture = Promise.promise();

        client.queryWithParams(query, params, result -> {
            if (result.failed()) {
                queryResultFuture.fail(result.cause());
                Logger.debug(DBConnector.class.toString() + " fail future: result: " + result.cause().toString());
            } else {
                queryResultFuture.complete(result.result());
                Logger.debug(DBConnector.class.toString() + " complete future");
            }
        });

        return queryResultFuture.future();
    }
}
