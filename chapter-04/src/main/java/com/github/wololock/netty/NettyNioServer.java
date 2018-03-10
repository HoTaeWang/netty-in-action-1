package com.github.wololock.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.time.LocalTime;

public class NettyNioServer {

  public void server(int port) throws Exception {
    final ByteBuf buf = Unpooled.unreleasableBuffer(
      Unpooled.copiedBuffer("Hi!\r\n", Charset.forName("UTF-8"))
    );
    final EventLoopGroup group = new NioEventLoopGroup(1);

    try {
      final ServerBootstrap b = new ServerBootstrap();
      b.group(group)
        .channel(NioServerSocketChannel.class)
        .localAddress(new InetSocketAddress(port))
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel channel) {
           channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
             @Override
             public void channelActive(ChannelHandlerContext ctx) {
               System.out.printf("[%25s] (%s) Channel active...\n", Thread.currentThread().getName(), LocalTime.now().toString());
               ctx.writeAndFlush(buf.duplicate())
                 .addListener(ChannelFutureListener.CLOSE);
             }
           });
          }
        });

      ChannelFuture f = b.bind().sync();
      f.channel().closeFuture().sync();
    } finally {
      group.shutdownGracefully().sync();
    }
  }

  public static void main(String[] args) throws Exception {
    new NettyNioServer().server(8999);
  }
}
