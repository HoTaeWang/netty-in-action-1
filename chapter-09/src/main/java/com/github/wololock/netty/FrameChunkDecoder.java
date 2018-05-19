package com.github.wololock.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

final class FrameChunkDecoder extends ByteToMessageDecoder {
  private final int maxFrameSize;

  public FrameChunkDecoder(int maxFrameSize) {
    this.maxFrameSize = maxFrameSize;
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
    final int readableBytes = in.readableBytes();
    if (readableBytes > maxFrameSize) {
      in.clear();
      throw new IllegalStateException(String.format("Number of readable bytes (%d) is greater than max frame size (%d)", readableBytes, maxFrameSize));
    }

    final ByteBuf buf = in.readBytes(readableBytes);
    out.add(buf);
  }
}
