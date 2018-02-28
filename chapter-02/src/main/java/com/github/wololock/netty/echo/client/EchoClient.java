package com.github.wololock.netty.echo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public final class EchoClient {

  public static void main(String[] args) throws InterruptedException {
    final EventLoopGroup group = new NioEventLoopGroup();
    try {
      final Bootstrap bootstrap = new Bootstrap();
      bootstrap.group(group)
        .channel(NioSocketChannel.class)
        .remoteAddress(new InetSocketAddress("localhost", 8787))
        .handler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel socketChannel) {
            socketChannel.pipeline().addLast(new EchoClientHandler());
          }
        });

      final ChannelFuture future = bootstrap.connect().sync();
      future.channel().closeFuture().sync();

    } finally {
      group.shutdownGracefully().sync();
    }
  }
}
