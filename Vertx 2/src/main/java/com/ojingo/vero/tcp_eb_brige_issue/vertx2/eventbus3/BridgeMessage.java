package com.ojingo.vero.tcp_eb_brige_issue.vertx2.eventbus3;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.eventbus.ReplyException;
import org.vertx.java.core.eventbus.ReplyFailure;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

/**
 * Message wrapper
 *
 * @author <a href="mailto:plopes@redhat.com">Paulo Lopes</a>
 */
public class BridgeMessage implements Message<JsonObject> {

  private final EventBus3 eb;
  private final String replyAddress;
  private final String address;
  private final boolean failure;

  private final JsonObject json;

  BridgeMessage(EventBus3 eb, JsonObject json) {
    this.json = json;
    this.eb = eb;

    if (json.containsField("replyAddress")) {
      replyAddress = json.getString("replyAddress");
    } else {
      replyAddress = null;
    }
    if (json.containsField("address")) {
      address = json.getString("address");
    } else {
      address = null;
    }
    failure = "err".equals(json.getString("type"));
  }

  public boolean succeeded() {
    return !failure;
  }

  public boolean failed() {
    return failure;
  }

  public void reply(JsonObject message, JsonObject headers, Handler<Message<JsonObject>> callback) {
    eb.send(replyAddress, message, headers, callback);
  }

  public void reply(JsonObject message, JsonObject headers) {
    eb.send(replyAddress, message, headers);
  }

  public void reply(JsonObject message) {
    eb.send(replyAddress, message);
  }

  @Override
  public void reply(JsonArray jsonArray) {
    eb.send(replyAddress, jsonArray);
  }

  @Override
  public void reply(String s) {
    eb.send(replyAddress, s);

  }

  @Override
  public void reply(Buffer buffer) {
    eb.send(replyAddress, buffer);

  }

  @Override
  public void reply(byte[] bytes) {
    eb.send(replyAddress, bytes);

  }

  @Override
  public void reply(Integer integer) {
    eb.send(replyAddress, integer);

  }

  @Override
  public void reply(Long aLong) {
    eb.send(replyAddress, aLong);

  }

  @Override
  public void reply(Short aShort) {
    eb.send(replyAddress, aShort);

  }

  @Override
  public void reply(Character character) {
    eb.send(replyAddress, character);

  }

  @Override
  public void reply(Boolean aBoolean) {
    eb.send(replyAddress, aBoolean);

  }

  @Override
  public void reply(Float aFloat) {
    eb.send(replyAddress, aFloat);

  }

  @Override
  public void reply(Double aDouble) {
    eb.send(replyAddress, aDouble);

  }

  @Override
  public <T> void reply(final Handler<Message<T>> handler) {
    Handler<Message<JsonObject>> wrapper = new Handler<Message<JsonObject>>() {

      @Override
      public void handle(Message msg) {
        handler.handle(msg);
      }
    };
    eb.send(replyAddress, null, wrapper);
  }

  @Override
  public <T> void replyWithTimeout(long l, Handler<AsyncResult<Message<T>>> handler) {
    fail(1, "Not implemented");

  }

  @Override
  public <T> void reply(Object o, final Handler<Message<T>> handler) {
    Handler<Message<JsonObject>> wrapper = new Handler<Message<JsonObject>>() {

      @Override
      public void handle(Message msg) {
        handler.handle(msg);
      }
    };
    eb.send(replyAddress, o, wrapper);
  }

  @Override
  public <T> void replyWithTimeout(Object o, long l, Handler<AsyncResult<Message<T>>> handler) {
    fail(1, "Not implemented");

  }

  @Override
  public <T> void reply(JsonObject jsonObject, final Handler<Message<T>> handler) {
    Handler<Message<JsonObject>> wrapper = new Handler<Message<JsonObject>>() {

      @Override
      public void handle(Message msg) {
        handler.handle(msg);
      }
    };
    eb.send(replyAddress, jsonObject, wrapper);
  }

  @Override
  public <T> void replyWithTimeout(JsonObject jsonObject, long l, Handler<AsyncResult<Message<T>>> handler) {
    fail(1, "Not implemented");

  }

  @Override
  public <T> void reply(JsonArray jsonArray, final Handler<Message<T>> handler) {
    Handler<Message<JsonObject>> wrapper = new Handler<Message<JsonObject>>() {

      @Override
      public void handle(Message msg) {
        handler.handle(msg);
      }
    };
    eb.send(replyAddress, jsonArray, wrapper);
  }

  @Override
  public <T> void replyWithTimeout(JsonArray jsonArray, long l, Handler<AsyncResult<Message<T>>> handler) {
    fail(1, "Not implemented");

  }

  @Override
  public <T> void reply(String s, final Handler<Message<T>> handler) {
    Handler<Message<JsonObject>> wrapper = new Handler<Message<JsonObject>>() {

      @Override
      public void handle(Message msg) {
        handler.handle(msg);
      }
    };
    eb.send(replyAddress, s, wrapper);
  }

  @Override
  public <T> void replyWithTimeout(String s, long l, Handler<AsyncResult<Message<T>>> handler) {
    fail(1, "Not implemented");

  }

  @Override
  public <T> void reply(Buffer buffer, final Handler<Message<T>> handler) {
    Handler<Message<JsonObject>> wrapper = new Handler<Message<JsonObject>>() {

      @Override
      public void handle(Message msg) {
        handler.handle(msg);
      }
    };
    eb.send(replyAddress, buffer, wrapper);
  }

  @Override
  public <T> void replyWithTimeout(Buffer buffer, long l, Handler<AsyncResult<Message<T>>> handler) {
    fail(1, "Not implemented");

  }

  @Override
  public <T> void reply(byte[] bytes, final Handler<Message<T>> handler) {

    Handler<Message<JsonObject>> wrapper = new Handler<Message<JsonObject>>() {

      @Override
      public void handle(Message msg) {
        handler.handle(msg);
      }
    };
    eb.send(replyAddress, bytes, wrapper);
  }

  @Override
  public <T> void replyWithTimeout(byte[] bytes, long l, Handler<AsyncResult<Message<T>>> handler) {
    fail(1, "Not implemented");

  }

  @Override
  public <T> void reply(Integer integer, final Handler<Message<T>> handler) {

    Handler<Message<JsonObject>> wrapper = new Handler<Message<JsonObject>>() {

      @Override
      public void handle(Message msg) {
        handler.handle(msg);
      }
    };
    eb.send(replyAddress, integer, wrapper);
  }

  @Override
  public <T> void replyWithTimeout(Integer integer, long l, Handler<AsyncResult<Message<T>>> handler) {
    fail(1, "Not implemented");

  }

  @Override
  public <T> void reply(Long aLong, final Handler<Message<T>> handler) {
    Handler<Message<JsonObject>> wrapper = new Handler<Message<JsonObject>>() {

      @Override
      public void handle(Message msg) {
        handler.handle(msg);
      }
    };
    eb.send(replyAddress, aLong, wrapper);
  }

  @Override
  public <T> void replyWithTimeout(Long aLong, long l, Handler<AsyncResult<Message<T>>> handler) {
    fail(1, "Not implemented");

  }

  @Override
  public <T> void reply(Short aShort, final Handler<Message<T>> handler) {
    Handler<Message<JsonObject>> wrapper = new Handler<Message<JsonObject>>() {

      @Override
      public void handle(Message msg) {
        handler.handle(msg);
      }
    };
    eb.send(replyAddress, aShort, wrapper);
  }

  @Override
  public <T> void replyWithTimeout(Short aShort, long l, Handler<AsyncResult<Message<T>>> handler) {
    fail(1, "Not implemented");

  }

  @Override
  public <T> void reply(Character character, final Handler<Message<T>> handler) {
    Handler<Message<JsonObject>> wrapper = new Handler<Message<JsonObject>>() {

      @Override
      public void handle(Message msg) {
        handler.handle(msg);
      }
    };
    eb.send(replyAddress, character, wrapper);
  }

  @Override
  public <T> void replyWithTimeout(Character character, long l, Handler<AsyncResult<Message<T>>> handler) {
    fail(1, "Not implemented");

  }

  @Override
  public <T> void reply(Boolean aBoolean, final Handler<Message<T>> handler) {
    Handler<Message<JsonObject>> wrapper = new Handler<Message<JsonObject>>() {

      @Override
      public void handle(Message msg) {
        handler.handle(msg);
      }
    };
    eb.send(replyAddress, aBoolean, wrapper);
  }

  @Override
  public <T> void replyWithTimeout(Boolean aBoolean, long l, Handler<AsyncResult<Message<T>>> handler) {
    fail(1, "Not implemented");
  }

  @Override
  public <T> void reply(Float aFloat, final Handler<Message<T>> handler) {
    Handler<Message<JsonObject>> wrapper = new Handler<Message<JsonObject>>() {

      @Override
      public void handle(Message msg) {
        handler.handle(msg);
      }
    };
    eb.send(replyAddress, aFloat, wrapper);

  }

  @Override
  public <T> void replyWithTimeout(Float aFloat, long l, Handler<AsyncResult<Message<T>>> handler) {
    fail(1, "Not implemented");
  }

  @Override
  public <T> void reply(Double aDouble, final Handler<Message<T>> handler) {
    Handler<Message<JsonObject>> wrapper = new Handler<Message<JsonObject>>() {

      @Override
      public void handle(Message msg) {
        handler.handle(msg);
      }
    };
    eb.send(replyAddress, aDouble, wrapper);
  }

  @Override
  public <T> void replyWithTimeout(Double aDouble, long l, Handler<AsyncResult<Message<T>>> handler) {
    fail(1, "Not implemented");
  }

  @Override
  public void fail(int i, String s) {
    eb.send(replyAddress, new ReplyException(ReplyFailure.RECIPIENT_FAILURE, i, s));
  }

  public ReplyException cause() {
    if (failed()) {
      return new ReplyException(
        ReplyFailure.fromInt(json.getInteger("failureType", 2)),
        json.getInteger("failureCode"),
        json.getString("message"));
    }

    return null;
  }

  @Override
  public String address() {
    return address;
  }

  @Override
  public JsonObject body() {
    return json.getObject("body");
  }

  @Override
  public String replyAddress() {
    return replyAddress;
  }

  @Override
  public void reply() {
    eb.send(replyAddress, null);
  }

  @Override
  public void reply(Object o) {
    eb.send(replyAddress, o);
  }

  public JsonObject headers() {
    return json.getObject("headers");
  }
}