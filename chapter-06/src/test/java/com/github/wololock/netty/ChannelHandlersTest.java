package com.github.wololock.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class ChannelHandlersTest {

  @Test
  public void executingHandlersInPipelineExample() {
    final AtomicInteger counter = new AtomicInteger(0);

    final Channel channel = new EmbeddedChannel();
    channel.pipeline().addLast("test", new ChannelInboundHandlerAdapter() {
      @Override
      public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.printf("[%s] msg1: %s\n", Thread.currentThread().getName(), msg);
        counter.incrementAndGet();
        ctx.fireChannelRead(msg);
      }
    });
    channel.pipeline().addLast("test2", new ChannelInboundHandlerAdapter() {
      @Override
      public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.printf("[%s] msg2: %s\n", Thread.currentThread().getName(), msg);
        counter.incrementAndGet();
      }
    });

    channel.pipeline().fireChannelRead("test");
    channel.close();

    assertThat(counter.get()).isEqualTo(2);
  }
}
