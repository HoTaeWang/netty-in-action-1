package com.github.wololock.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

final class IntegerToByteCodec extends ByteToMessageCodec<Integer> {

  @Override
  protected void encode(ChannelHandlerContext ctx, Integer msg, ByteBuf out) {
    out.writeInt(msg);
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
    out.add(in.readInt());
  }
}
