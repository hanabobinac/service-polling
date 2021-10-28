package se.company.services.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import se.company.services.utils.Logger;
import se.company.services.database.ServiceDB;
import se.company.services.models.ServiceRecord;


public class ServiceVerticle extends AbstractVerticle {

    private final ServiceDB serviceDb;

    public ServiceVerticle(ServiceDB serviceDb) {
        this.serviceDb = serviceDb;
    }

    @Override
    public void start() {
        Logger.debug(ServiceVerticle.class.toString());
        vertx.eventBus().consumer("services.get", this::get);
        vertx.eventBus().consumer("services.getByUrl", this::getByUrl);
        vertx.eventBus().consumer("services.post", this::post);
        vertx.eventBus().consumer("services.patch", this::patch);
        vertx.eventBus().consumer("services.delete", this::delete);
    }

    private void get(Message msg) {
        Logger.debug("ServiceVerticle.get()");

        var result = serviceDb.getAllServices().onComplete(r -> {
            if (r.succeeded()) {
                var response = r.result().getResults();
                var jsonArray = new JsonArray();
                for (JsonArray item : response) {
                    jsonArray.add(new ServiceRecord(item).toJson());
                }
                msg.reply(jsonArray.toString());
            } else {
                msg.reply("Fail!");
            }
        });
    }

    private void getByUrl(Message msg) {
        Logger.debug("ServiceVerticle.getByUrl()");

        JsonObject json = (JsonObject) msg.body();
        ServiceRecord serviceRecord = new ServiceRecord(json);
        var result = serviceDb.getServiceByUrl(serviceRecord.url()).onComplete(r -> {
            if (r.succeeded()) {
                var response = r.result().getResults();
                var jsonArray = new JsonArray();
                for (JsonArray item : response) {
                    jsonArray.add(new ServiceRecord(item).toJson());
                }
                msg.reply(jsonArray.toString());
            } else {
                msg.reply("Fail!");
            }
        });
    }

    private void post(Message msg) {
        Logger.debug("ServiceVerticle.post()");

        JsonObject json = (JsonObject) msg.body();
        ServiceRecord serviceRecord = new ServiceRecord(json);
        var result = serviceDb.addService(serviceRecord).onComplete(r -> {
            if (r.succeeded()) {
                var rs = r.result();
                Logger.debug(ServiceVerticle.class.toString() + " post Result: " + rs);
                msg.reply(Boolean.TRUE);
            } else {
                Logger.debug(ServiceVerticle.class.toString() + " post fail.");
                DeliveryOptions options = new DeliveryOptions();
                options.addHeader("HttpStatusCode", "409");
                msg.reply(Boolean.FALSE, options);
            }
        });
    }

    private void patch(Message msg) {
        Logger.debug("ServiceVerticle.patch()");

        ServiceRecord serviceRecord = new ServiceRecord((JsonObject) msg.body());
        var result = serviceDb.getServiceByUrl(serviceRecord.url()).onComplete(r -> {
            if (r.succeeded()) {
                var response = r.result().getResults();
                if (response.isEmpty()) {
                    DeliveryOptions options = new DeliveryOptions();
                    options.addHeader("HttpStatusCode", "404");
                    msg.reply(Boolean.FALSE, options);
                } else {
                    var resultUpdate = serviceDb.updateServiceName(serviceRecord).onComplete(rUpdate -> {
                        if (rUpdate.succeeded()) {
                            msg.reply(Boolean.TRUE);
                        } else {
                            Logger.warn("updateServiceName failed!");
                            DeliveryOptions options = new DeliveryOptions();
                            options.addHeader("HttpStatusCode", "500");
                            msg.reply(Boolean.FALSE, options);
                        }
                    });
                }
            } else {
                Logger.error(ServiceVerticle.class.toString() + " patch update failed!");
            }
        });
    }

    private void delete(Message msg) {
        Logger.debug("ServiceVerticle.delete()");

        JsonObject json = (JsonObject) msg.body();
        ServiceRecord serviceRecord = new ServiceRecord(json);
        var result = serviceDb.deleteService(serviceRecord).onComplete(r -> {
            if (r.succeeded()) {
                var rs = r.result();
                msg.reply("OK");
            } else {
                msg.reply("Fail!");
            }
        });
    }
}