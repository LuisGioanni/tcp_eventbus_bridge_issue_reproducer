package com.ojingo.vero.tcp_eb_bridge_issue.vertx3.bridge;

import java.util.logging.Logger;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.bridge.BridgeOptions;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.eventbus.bridge.tcp.TcpEventBusBridge;

/**
 * Created by luis on 03/06/16.
 */
public class BridgeVerticle extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    TcpEventBusBridge bridge = TcpEventBusBridge.create(vertx,
      new BridgeOptions().addInboundPermitted(new PermittedOptions()).addOutboundPermitted(new PermittedOptions()));
    bridge.listen(7000);
    vertx.eventBus().addInterceptor(sendContext -> {
      Logger.getGlobal().info("address : " + sendContext.message().address());
      Logger.getGlobal().info("replyAddress : " + sendContext.message().replyAddress());
      if (sendContext.message().body() != null)
        Logger.getGlobal().info("body : " + sendContext.message().body().toString());
      sendContext.next();
    });
  }
}
