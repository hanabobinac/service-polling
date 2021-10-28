package se.company.services;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;
import se.company.services.database.DBConnector;
import se.company.services.database.DBCreator;
import se.company.services.database.ServiceDB;
import se.company.services.utils.Logger;
import se.company.services.verticles.BackgroundPoller;
import se.company.services.verticles.ServiceVerticle;


public class MainVerticle {

    private Vertx vertx;
    private ServiceDB serviceDB;
    private BackgroundPoller poller;

    public static void main(String[] args) {
        new MainVerticle().start();
    }

    public void start() {
        Logger.debug(MainVerticle.class.toString());
        vertx = Vertx.vertx();
        initDatabase();
        initRoutes();
        initPoller();
    }

    private void initDatabase() {
        DBConnector database = new DBConnector(vertx);
        DBCreator dbcreator = new DBCreator(database);
        dbcreator.createTables();

        this.serviceDB = new ServiceDB(database);
        vertx.deployVerticle(new ServiceVerticle(serviceDB));
    }

    private void initPoller() {
        poller = new BackgroundPoller(serviceDB);
        vertx.setPeriodic(1000 * 200, this::timerHandler);
    }

    private void initRoutes() {
        HttpServer httpServer = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.get("/services").handler(this::getAllServices);
        router.get("/services/:url").handler(this::getServiceById);
        router.post("/services/:url/:name").handler(this::postService);
        router.patch("/services/:url/:name").handler(this::patchService);
        router.delete("/services/:url").handler(this::deleteService);

        router.route().handler(CorsHandler.create("*")
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.PUT)
                .allowedMethod(HttpMethod.PATCH)
                .allowedMethod(HttpMethod.DELETE)
                .allowCredentials(true)
                .allowedHeader("Access-Control-Allow-Origin")
                .allowedHeader("Access-Control-Allow-Methods")
                .allowedHeader("Access-Control-Allow-Credentials")
                .allowedHeader("Content-Type")
        );

        router.route().handler(StaticHandler.create());

        httpServer.requestHandler(router).listen(8080);
    }

    void getAllServices(RoutingContext ctx) {
        Logger.debug("MainVerticle.getAllServices()");

        vertx.eventBus().request("services.get", "", reply -> {
            ctx.request().response()
                    .putHeader("Access-Control-Allow-Origin", "*")
                    .putHeader("Content-Type", "application/json")
                    .end((String) reply.result().body());
        });
    }

    void getServiceById(RoutingContext ctx) {
        Logger.debug("MainVerticle.getServiceById()");

        JsonObject json = new JsonObject();
        json.put("url", ctx.pathParam("url"));
        vertx.eventBus().request("services.getByUrl", json, reply -> {
            ctx.request().response()
                    .putHeader("Access-Control-Allow-Origin", "*")
                    .putHeader("Content-Type", "application/json")
                    .end((String) reply.result().body());
        });
    }

    void postService(RoutingContext ctx) {
        Logger.debug("MainVerticle.postService()");

        JsonObject json = new JsonObject();
        json.put("url", ctx.pathParam("url"));
        json.put("name", ctx.pathParam("name"));
        vertx.eventBus().request("services.post", json, reply -> {
            var r = (Boolean) reply.result().body();
            var h = reply.result().headers();
            Logger.debug("MainVerticle postService reply.result().body(): " + r);
            if (r == Boolean.TRUE) {
                ctx.request().response()
                        .putHeader("Access-Control-Allow-Origin", "*")
                        .putHeader("Content-Type", "application/json")
                        .end("OK");
            } else {
                var codeString = reply.result().headers().get("HttpStatusCode");
                var code = Integer.parseInt(codeString);
                Logger.debug("MainVerticle postService reply.result().headers(): " + h);
                ctx.request().response().setStatusCode(code).
                        end("Service already exists! Insert failed. Use PATCH method to update service name.");
            }
        });
    }

    void patchService(RoutingContext ctx) {
        Logger.debug("MainVerticle.patchService()");

        JsonObject json = new JsonObject();
        json.put("url", ctx.pathParam("url"));
        json.put("name", ctx.pathParam("name"));
        vertx.eventBus().request("services.patch", json, reply -> {
            var r = (Boolean) reply.result().body();
            var h = reply.result().headers();
            Logger.debug(MainVerticle.class.toString() + "  patchService reply.result().body(): " + r);
            if (r == Boolean.TRUE) {
                ctx.request().response()
                        .putHeader("Access-Control-Allow-Origin", "*")
                        .putHeader("Content-Type", "text/html")
                        .end("OK");
            } else {
                var codeString = reply.result().headers().get("HttpStatusCode");
                var code = Integer.parseInt(codeString);
                Logger.debug(MainVerticle.class.toString() + "  patchService reply.result().headers(): " + h);
                ctx.request().response()
                        .putHeader("Access-Control-Allow-Origin", "*")
                        .putHeader("Content-Type", "text/html")
                        .setStatusCode(code)
                        .end("Service does not exist! Update failed. Use POST method to add service.");
            }
        });
    }

    void deleteService(RoutingContext ctx) {
        Logger.debug("MainVerticle.deleteService()");

        JsonObject json = new JsonObject();
        json.put("url", ctx.pathParam("url"));
        vertx.eventBus().request("services.delete", json, reply -> {
            ctx.request().response()
                    .putHeader("Access-Control-Allow-Origin", "*")
                    .putHeader("Content-Type", "text/html")
                    .end((String) reply.result().body());
        });
    }

    void timerHandler(Long timerId) {
        if (poller.pollServices().succeeded()) {
            System.out.println("timerId=" + timerId);
            System.out.println("pollServices OK");
        } else {
            System.out.println("pollServices FAIL!!!");
        }
    }
}
