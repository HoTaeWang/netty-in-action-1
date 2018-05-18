package com.github.wololock.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AbsIntegerEncoderTest {

  @Test
  public void testEncoded() {
    //given:
    final ByteBuf buf = Unpooled.buffer();
    for (int i = 1; i < 10; i++) {
      buf.writeInt(i * -1);
    }

    //when:
    final EmbeddedChannel channel = new EmbeddedChannel(new AbsIntegerEncoder());

    //then:
    assertThat(channel.writeOutbound(buf)).isTrue();
    assertThat(channel.finish()).isTrue();

    //when: "read bytes"
    for (int i = 1; i < 10; i++) {
      assertThat(((Integer) channel.readOutbound())).isEqualTo(i);
    }

    assertThat(((Integer) channel.readOutbound())).isNull();
  }
}
