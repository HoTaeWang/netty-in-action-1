package com.github.wololock.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class FixedLengthFrameDecoderTest {


  @Test
  public void testFramesDecode() {
    //given:
    final ByteBuf buf = Unpooled.buffer();
    IntStream.range(0, 9).forEach(buf::writeByte);
    final ByteBuf input = buf.duplicate();

    //when:
    final EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));

    //then:
    assertThat(channel.writeInbound(input.retain())).isTrue();
    assertThat(channel.finish()).isTrue();

    //when:
    ByteBuf read = channel.readInbound();

    //then:
    assertThat(buf.readSlice(3)).isEqualTo(read);
    read.release();

    //when: "read another 3 bytes"
    read = channel.readInbound();

    //then:
    assertThat(buf.readSlice(3)).isEqualTo(read);
    read.release();

    //when: "read another 3 bytes"
    read = channel.readInbound();

    //then:
    assertThat(buf.readSlice(3)).isEqualTo(read);
    read.release();

    // there are no more 3 bytes ready to read
    assertThat((ByteBuf) channel.readInbound()).isNull();
    buf.release();
  }

  @Test
  public void testAnotherFrameDecode() {
    //given:
    final ByteBuf buf = Unpooled.buffer();
    IntStream.range(0, 9).forEach(buf::writeByte);
    final ByteBuf input = buf.duplicate();

    //when:
    final EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));

    //then:
    assertThat(channel.writeInbound(input.readBytes(2))).isFalse();
    assertThat(channel.writeInbound(input.readBytes(7))).isTrue();
    assertThat(channel.finish()).isTrue();

    //when:
    ByteBuf read = channel.readInbound();

    //then:
    assertThat(buf.readSlice(3)).isEqualTo(read);
    read.release();

    //when: "read another 3 bytes"
    read = channel.readInbound();

    //then:
    assertThat(buf.readSlice(3)).isEqualTo(read);
    read.release();

    //when: "read another 3 bytes"
    read = channel.readInbound();

    //then:
    assertThat(buf.readSlice(3)).isEqualTo(read);
    read.release();

    // there are no more 3 bytes ready to read
    assertThat((ByteBuf) channel.readInbound()).isNull();
    buf.release();
  }
}
