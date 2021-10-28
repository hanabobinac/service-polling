package se.company.services.verticles;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import se.company.services.database.ServiceDB;
import se.company.services.models.ServiceRecord;
import se.company.services.utils.Logger;


public class BackgroundPoller {

    private ServiceDB serviceDB;

    public BackgroundPoller(ServiceDB serviceDB) {
        this.serviceDB = serviceDB;
    }

    public Future<List<String>> pollServices() {

        serviceDB.getAllServices().onComplete(
                r -> {
                    if (r.succeeded()) {
                        List<JsonArray> list = r.result().getResults();
                        for (JsonArray arr : list) {
                            ServiceRecord serviceRecord = new ServiceRecord(arr);
                            serviceRecord.updated(new Date());
                            serviceRecord.status(isReachableByHttpGet(serviceRecord.url()));
                            serviceDB.updateServiceStatus(serviceRecord);
                        }
                    }
                }
        );
        return Future.succeededFuture();
    }

    public static boolean isReachableByHttpGet(String host) {
        try {
            URL url = new URL("https://" + host);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            int status = con.getResponseCode();
            Logger.debug("status: " + status);

            con.disconnect();

            return status == 200;
        } catch (Exception e) {
            Logger.debug("Error during http get: " + e.getMessage());
            return false;
        }
    }
}
