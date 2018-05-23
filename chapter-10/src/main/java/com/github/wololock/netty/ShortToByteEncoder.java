package com.github.wololock.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

final class ShortToByteEncoder extends MessageToByteEncoder<Short> {
  @Override
  protected void encode(ChannelHandlerContext ctx, Short msg, ByteBuf out) {
    out.writeShort(msg);
  }
}
