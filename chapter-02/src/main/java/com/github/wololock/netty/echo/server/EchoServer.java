package com.github.wololock.netty.echo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public final class EchoServer {

  public static void main(String[] args) throws InterruptedException {
    final EchoServerHandler handler = new EchoServerHandler();
    final EventLoopGroup group = new NioEventLoopGroup();
    try {
      final ServerBootstrap serverBootstrap = new ServerBootstrap();
      serverBootstrap.group(group)
        .channel(NioServerSocketChannel.class)
        .localAddress(new InetSocketAddress(8787))
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel socketChannel) {
            socketChannel.pipeline().addLast(handler);
          }
        });

      final ChannelFuture future = serverBootstrap.bind().sync();
      future.channel().closeFuture().sync();
    } finally {
      group.shutdownGracefully().sync();
    }
  }
}
