package com.ojingo.vero.tcp_eb_bridge_issue.vertx3;

import java.util.logging.Logger;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

/**
 * Created by luis on 03/06/16.
 */
public class Vertx3Verticle extends AbstractVerticle {

  @Override
  public void start(Future fut) throws Exception {

    Router router = Router.router(vertx);
    router.get("/vertx2").handler(routingContext -> {
      vertx.eventBus().send("test", new JsonObject(), ar -> {
        if (ar.succeeded()) {
          routingContext.response().end((String) ar.result().body());
        } else {
          routingContext.response().setStatusCode(500).end("Error");
        }
      });
    });

    HttpServer server = vertx.createHttpServer()
      .requestHandler(router::accept)
      .listen(8080, httpServer -> {
      if (httpServer.succeeded()) {
        Logger.getGlobal().info("HTTP server launched");
        fut.complete();
      } else {
        fut.fail(httpServer.cause());
      }

    });
  }
}
