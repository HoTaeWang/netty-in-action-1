package com.github.wololock.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

final class IntegerToStringEncoder extends MessageToMessageEncoder<Integer> {
  @Override
  protected void encode(ChannelHandlerContext ctx, Integer msg, List<Object> out) {
    out.add(String.valueOf(msg));
  }
}
