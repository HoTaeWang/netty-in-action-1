package com.github.wololock.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.Arrays;

public class ByteBufUsagePatternsTest {

  @Test
  public void heapBuffers() {
    ByteBuf heapBuf = Unpooled.copiedBuffer("Heap buffer test", Charset.forName("UTF-8"));

    if (heapBuf.hasArray()) {
      byte[] array = heapBuf.array();
      int offset = heapBuf.arrayOffset() + heapBuf.readerIndex();
      int length = heapBuf.readableBytes();

      System.out.println("bytes array: " + Arrays.toString(array));
      System.out.println("offset: " + offset);
      System.out.println("length: " + length);
    }
  }
}
