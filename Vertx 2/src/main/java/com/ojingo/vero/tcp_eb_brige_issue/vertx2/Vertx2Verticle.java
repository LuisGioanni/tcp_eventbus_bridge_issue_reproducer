package com.ojingo.vero.tcp_eb_brige_issue.vertx2;

import java.util.logging.Logger;

import com.ojingo.vero.tcp_eb_brige_issue.vertx2.eventbus3.EventBus3;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/**
 * Created by luis on 03/06/16.
 */
public class Vertx2Verticle extends Verticle{

  EventBus3 eb3;
  @Override
  public void start() {
    eb3 = new EventBus3(vertx, new JsonObject().putString("host", "localhost"), new Handler<Void>() {

      @Override
      public void handle(Void aVoid) {
       eb3.registerHandler("test", new Handler<Message<JsonObject>>() {

         @Override
         public void handle(Message<JsonObject> jsonObjectMessage) {
           jsonObjectMessage.reply("Message from vertx 2");
         }
       });
      }
    });
    eb3.exceptionHandler(new Handler<Throwable>() {

      @Override
      public void handle(Throwable throwable) {
        throwable.printStackTrace();
      }
    });
  }
}
