package se.company.services.verticles;

import java.util.Date;
import java.util.List;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import se.company.services.database.ServiceDB;
import se.company.services.models.ServiceRecord;


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
                            serviceRecord.date(new Date());
                            serviceRecord.status(isReachableByPing(serviceRecord.url()));
                            serviceDB.updateServiceStatus(serviceRecord);
                        }
                    }
                }
        );
        return Future.succeededFuture();
    }

    public static boolean isReachableByPing(String host) {
        try {
            String cmd = "";
            if (System.getProperty("os.name").startsWith("Windows")) {
                // For Windows
                cmd = "ping -n 1 " + host;
            } else {
                // For Linux and OSX
                cmd = "ping -c 1 " + host;
            }

            Process myProcess = Runtime.getRuntime().exec(cmd);
            myProcess.waitFor();

            if (myProcess.exitValue() == 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
