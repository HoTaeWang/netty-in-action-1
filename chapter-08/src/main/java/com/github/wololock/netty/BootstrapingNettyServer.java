package com.github.wololock.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class BootstrapingNettyServer {

  public static void main(String[] args) throws InterruptedException {
    final ByteBuf buf = Unpooled.unreleasableBuffer(
      Unpooled.copiedBuffer("{\"foo\": \"bar\"}", Charset.forName("UTF-8"))
    );
    final EventLoopGroup group = new NioEventLoopGroup();
    final ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap.group(group)
      .channel(NioServerSocketChannel.class)
      .childHandler(new ChannelInitializer<SocketChannel>() {
        @Override
        protected void initChannel(SocketChannel channel) {
          channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
            @Override
            public void channelActive(ChannelHandlerContext ctx) {
              final HttpHeaders headers = new DefaultHttpHeaders();
              headers.add("Content-Type", "applicaiton/json");
              final DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf.duplicate());

              ctx.writeAndFlush(response)
                .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
          });
        }
      });

    final ChannelFuture future = bootstrap.bind(new InetSocketAddress(8080)).sync();
    future.channel().closeFuture().sync();
  }
}
