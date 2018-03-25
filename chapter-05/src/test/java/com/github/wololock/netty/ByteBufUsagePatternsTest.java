package com.github.wololock.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.nio.ByteBuffer;
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

  @Test
  public void directBuffers() {
    ByteBuf directBuf = Unpooled.directBuffer(16);
    directBuf.writeInt(1);
    directBuf.writeByte(1);

    if (!directBuf.hasArray()) {

      int length = directBuf.readableBytes();
      byte[] array = new byte[length];
      directBuf.getBytes(directBuf.readerIndex(), array);

      System.out.println("bytes array: " + Arrays.toString(array));
      System.out.println("length: " + length);
    }
  }

  @Test
  public void compositePatternWithByteBuffer() {
    final String headerText = "Content-Type: application/json";
    final String bodyText = "{\"test\": 2}";

    final ByteBuffer header = ByteBuffer.allocate(headerText.getBytes().length);
    final ByteBuffer body = ByteBuffer.allocate(bodyText.getBytes().length);

    header.put(headerText.getBytes(Charset.forName("UTF-8")));
    body.put(bodyText.getBytes(Charset.forName("UTF-8")));

    // Use an array to hold the message parts
    final ByteBuffer[] message = new ByteBuffer[] { header, body };

    // Create a new ByteBuffer and use copy to merge the header and body
    final ByteBuffer message2 = ByteBuffer.allocate(header.remaining() + body.remaining());
    message2.put(header);
    message2.put(body);
    message2.flip();

    System.out.println(message2.toString());
  }

  @Test
  public void compositePatternWithByteBuf() {
    final String headerText = "Content-Type: application/json";
    final String bodyText = "{\"test\": 2}";

    final CompositeByteBuf messageBuf = Unpooled.compositeBuffer();
    final ByteBuf header = Unpooled.copiedBuffer(headerText, Charset.forName("UTF-8"));
    final ByteBuf body = Unpooled.copiedBuffer(bodyText, Charset.forName("UTF-8"));

    messageBuf.addComponents(header, body);

    messageBuf.forEach(buf -> {
      System.out.println(buf.toString());
    });

    // Accessing the data
    int length = messageBuf.readableBytes();
    byte[] array = new byte[length];
    messageBuf.getBytes(messageBuf.readerIndex(), array);

    System.out.println(Arrays.toString(array));
  }

  @Test
  public void randomAccessExample() {
    final ByteBuf buf = Unpooled.copiedBuffer("Test", Charset.forName("UTF-8"));

    for (int i = 0; i < buf.capacity(); i++) {
      byte b = buf.getByte(i);
      System.out.println((char) b);
    }
  }

  @Test
  public void sequentialAccessIndexing() {
    final ByteBuf buf = Unpooled.copiedBuffer("Lorem ipsum", Charset.forName("UTF-8"));

    System.out.println("readable bytes: " + buf.readableBytes());
    System.out.println("writable bytes: " + buf.writableBytes());
    System.out.println("bytes read: " + buf.readerIndex());

    while (buf.isReadable()) {
      char character = (char) buf.readByte();
      int readableBytes = buf.readableBytes();
      int writableBytes = buf.writableBytes();
      int bytesRead = buf.readerIndex();

      System.out.printf("%s (readable bytes: %d, writable bytes: %d, bytes read: %d)\n", character, readableBytes, writableBytes, bytesRead);
    }
  }
}
