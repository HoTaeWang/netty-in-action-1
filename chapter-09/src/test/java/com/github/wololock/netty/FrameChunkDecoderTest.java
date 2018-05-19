package com.github.wololock.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.DecoderException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class FrameChunkDecoderTest {

  @Test
  public void testFrameDecoder() {
    //given:
    final ByteBuf buf = Unpooled.buffer();
    for (int i = 0; i < 9; i++) {
      buf.writeByte(i);
    }
    final ByteBuf input = buf.duplicate();

    //when:
    final EmbeddedChannel channel = new EmbeddedChannel(new FrameChunkDecoder(3));

    //then:
    assertThat(channel.writeOutbound(input.readBytes(2))).isTrue();

    assertThatExceptionOfType(DecoderException.class)
      .isThrownBy(() -> channel.writeInbound(input.readBytes(4)));

    assertThat(channel.writeOutbound(input.readBytes(3))).isTrue();

    assertThat(channel.finish()).isTrue();

    //when: "Read frames"
    ByteBuf read = channel.readOutbound();

    //then:
    assertThat(buf.readSlice(2)).isEqualTo(read);
    read.release();

    read = channel.readOutbound();

    assertThat(buf.skipBytes(4).readSlice(3)).isEqualTo(read);

    read.release();
    buf.release();
  }
}
