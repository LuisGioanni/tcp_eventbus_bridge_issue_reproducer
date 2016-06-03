package com.ojingo.vero.tcp_eb_brige_issue.vertx2.eventbus3;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import com.ojingo.vero.tcp_eb_brige_issue.vertx2.eventbus3.BridgeMessage;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.net.NetClient;
import org.vertx.java.core.net.NetSocket;

/**
 * TCP EventBus bridge client for Vert.x3
 *
 * @author <a href="mailto:plopes@redhat.com">Paulo Lopes</a>
 */
public class EventBus3 {

  private static final JsonObject EMPTY_JSON = new JsonObject(Collections.EMPTY_MAP);

  private enum State {
    CONNECTING,
    OPEN,
    CLOSING,
    CLOSED
  }

  private final NetClient client;
  private final Map<String, List<Handler<Message<JsonObject>>>> handlers = new HashMap<>();
  private final Map<String, Handler<Message<JsonObject>>> replyHandlers = new HashMap<>();
  private final JsonObject defaultHeaders;

  private State state = State.CLOSED;
  private NetSocket transport;
  private Handler<Throwable> exceptionHandler;
  private Handler<Void> endHandler;

  public EventBus3(final Vertx vertx, JsonObject config, final Handler<Void> ready) {
    if (config == null) {
      config = new JsonObject();
    }

    // configuration
    int port = config.getInteger("port", 7000);
    String host = config.getString("host");
    Integer reconnectAttempts = config.getInteger("reconnectAttempts");
    Long reconnectInterval = config.getLong("reconnectInterval");
    Integer connectionTimeout = config.getInteger("connectionTimeout");
    final int pingInterval = config.getInteger("pingInterval", 5000);

    defaultHeaders = config.getObject("defaultHeaders", new JsonObject());

    if (host == null) {
      throw new RuntimeException("missing required config: [host]");
    }

    client = vertx.createNetClient();

    if (reconnectAttempts != null) {
      client.setReconnectAttempts(reconnectAttempts);
    }

    if (reconnectInterval != null) {
      client.setReconnectInterval(reconnectInterval);
    }

    if (connectionTimeout != null) {
      client.setConnectTimeout(connectionTimeout);
    }

    state = State.CONNECTING;

    client.connect(port, host, new Handler<AsyncResult<NetSocket>>() {

      @Override
      public void handle(AsyncResult<NetSocket> connect) {
        if (connect.failed()) {
          if(exceptionHandler != null) {
            exceptionHandler.handle(connect.cause());
          }
          return;
        }

        final NetSocket socket = connect.result();
        state = State.OPEN;
        transport = socket;

        // Send the first ping then send a ping every pingInterval milliseconds
        send(socket, new JsonObject().putString("type", "ping"));

        final long pingTimerID = vertx.setPeriodic(pingInterval, new Handler<Long>() {

          @Override
          public void handle(Long aLong) {
            if (state == State.OPEN) {
              send(socket, new JsonObject().putString("type", "ping"));
            }
          }
        });

        socket.endHandler(new Handler<Void>() {

          @Override
          public void handle(Void aVoid) {
            state = State.CLOSED;
            vertx.cancelTimer(pingTimerID);
            if (endHandler != null) {
              endHandler.handle(null);
            }
          }
        });

        socket.exceptionHandler(new Handler<Throwable>() {

          @Override
          public void handle(Throwable throwable) {
            state = State.CLOSED;
            vertx.cancelTimer(pingTimerID);
            if (exceptionHandler != null) {
              exceptionHandler.handle(throwable);
            }
          }
        });

        socket.dataHandler(new Handler<Buffer>() {

          private Buffer buffer = new Buffer(0);

          @Override
          public void handle(final Buffer buf) {
            buffer.appendBuffer(buf);
            while (buffer.length() > 4) {
              int len = buffer.getInt(0);
              if (buffer.length() >= len + 4) {
                String message = buffer.getString(4, 4 + len);
                // slice
                buffer = buffer.getBuffer(4 + len, buffer.length());

                JsonObject json;
                try {
                  json = new JsonObject(message);
                } catch (RuntimeException e) {
                  if (exceptionHandler != null) {
                    exceptionHandler.handle(e);
                  }
                  return;
                }

                final BridgeMessage bridgeMessage = new BridgeMessage(EventBus3.this, json);

                if (handlers.containsKey(json.getString("address"))) {
                  // iterate all registered handlers
                  List<Handler<Message<JsonObject>>> list = handlers.get(json.getString("address"));
                  for (Handler<Message<JsonObject>> handler : list) {
                    handler.handle(bridgeMessage);
                  }
                } else if (replyHandlers.containsKey(json.getString("address"))) {
                  // Might be a reply message
                  Handler<Message<JsonObject>> handler = replyHandlers.remove(json.getString("address"));
                  handler.handle(bridgeMessage);
                } else {
                  if (bridgeMessage.failed()) {
                    exceptionHandler.handle(bridgeMessage.cause());
                  }
                }
              }
            }
          }
        });

        if (ready != null) {
          ready.handle(null);
        }
      }
    });
  }

  public EventBus3(final Vertx vertx, JsonObject config) {
    this(vertx, config, null);
  }

  public void send(String address, Object message, JsonObject headers, Handler<Message<JsonObject>> callback) {
    // are we ready?
    if (this.state != State.OPEN) {
      exceptionHandler.handle(new RuntimeException("INVALID_STATE_ERR"));
      return;
    }

    if (address == null) {
      exceptionHandler.handle(new RuntimeException("NULL Address"));
      return;
    }

    JsonObject envelope = new JsonObject()
      .putString("type", "send")
      .putString("address", address)
      .putObject("headers", new JsonObject().mergeIn(defaultHeaders).mergeIn(headers))
      .putValue("body", message);

    if (callback != null) {
      String replyAddress = UUID.randomUUID().toString();
      envelope.putString("replyAddress", replyAddress);
      replyHandlers.put(replyAddress, callback);
    }

    send(transport, envelope);
  }

  public void send(String address, Object message, JsonObject headers) {
    send(address, message, headers, null);
  }

  public void send(String address, Object message) {
    Logger.getGlobal().info("About to send message over EB3 to " + address);
    send(address, message, EMPTY_JSON, null);
  }

  public void send(String address, Object message, Handler<Message<JsonObject>> callback) {
    send(address, message, EMPTY_JSON, callback);
  }

  public void publish(String address, Object message, JsonObject headers) {
    // are we ready?
    if (state != State.OPEN) {
      exceptionHandler.handle(new RuntimeException("INVALID_STATE_ERR"));
      return;
    }

    if (address == null) {
      exceptionHandler.handle(new RuntimeException("NULL Address"));
      return;
    }

    send(transport, new JsonObject()
      .putString("type", "publish")
      .putString("address", address)
      .putObject("headers", new JsonObject().mergeIn(defaultHeaders).mergeIn(headers))
      .putValue("body", message));
  }

  public void publish(String address, Object message) {
    publish(address, message, EMPTY_JSON);
  }

  public void registerHandler(String address, JsonObject headers, Handler<Message<JsonObject>> callback) {
    // are we ready?
    if (state != State.OPEN) {
      exceptionHandler.handle(new RuntimeException("INVALID_STATE_ERR"));
      return;
    }

    if (address == null) {
      exceptionHandler.handle(new RuntimeException("NULL Address"));
      return;
    }

    // ensure it is a list
    if (!handlers.containsKey(address)) {
      handlers.put(address, new LinkedList<Handler<Message<JsonObject>>>());
      // First handler for this address so we should register the connection
      send(transport, new JsonObject()
        .putString("type", "register")
        .putString("address", address)
        .putObject("headers", new JsonObject().mergeIn(defaultHeaders).mergeIn(headers)));
    }

    handlers.get(address).add(callback);
  }

  public void registerHandler(String address, Handler<Message<JsonObject>> callback) {
    registerHandler(address, EMPTY_JSON, callback);
  }

  public void unregisterHandler(String address, JsonObject headers, Handler<Message<JsonObject>> callback) {
    // are we ready?
    if (state != State.OPEN) {
      exceptionHandler.handle(new RuntimeException("INVALID_STATE_ERR"));
      return;
    }

    if (address == null) {
      exceptionHandler.handle(new RuntimeException("NULL Address"));
      return;
    }

    List<Handler<Message<JsonObject>>> handlers = this.handlers.get(address);

    if (handlers != null) {

      int idx = handlers.indexOf(callback);
      if (idx != -1) {
        handlers.remove(idx);
        if (handlers.size() == 0) {
          // No more local handlers so we should unregister the connection
          send(this.transport, new JsonObject()
            .putString("type", "unregister")
            .putString("address", address)
            .putObject("headers", new JsonObject().mergeIn(defaultHeaders).mergeIn(headers)));

          this.handlers.remove(address);
        }
      }
    }
  }

  public void unregisterHandler(String address, Handler<Message<JsonObject>> callback) {
    unregisterHandler(address, EMPTY_JSON, callback);
  }

  public void close() {
    state = State.CLOSING;
    transport.close();
    client.close();
  }

  public void exceptionHandler(Handler<Throwable> handler) {
    this.exceptionHandler = handler;
  }

  public void endHandler(Handler<Void> handler) {
    this.endHandler = handler;
  }

  private void send(NetSocket transport, JsonObject message) {
    final byte[] envelope = message.encode().getBytes();
    Buffer buffer = new Buffer(envelope.length + 4);
    buffer.appendInt(envelope.length);
    buffer.appendBytes(envelope);
    transport.write(buffer);
  }
}