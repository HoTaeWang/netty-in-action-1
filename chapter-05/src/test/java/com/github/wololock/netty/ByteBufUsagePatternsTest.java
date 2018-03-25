package com.github.wololock.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

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

  @Test
  public void writeRandomInts() {
    final ByteBuf buf = Unpooled.buffer(16, 16);
    final Random random = new Random();

    while (buf.writableBytes() >= 4) {
      buf.writeInt(random.nextInt(100));
    }

    while (buf.isReadable()) {
      System.out.println(buf.readInt());
    }
  }

  @Test
  public void readerIndexManagement() {
    //given:
    final ByteBuf buffer = Unpooled.copyInt(1,2,3,4,5,6);
    final List<Integer> result = new ArrayList<>();
    final List<Integer> expected = Arrays.asList(1,2,3,4,5,6,4,5,6);

    //when:
    result.add(buffer.readInt());
    result.add(buffer.readInt());
    result.add(buffer.readInt());

    buffer.markReaderIndex();

    result.add(buffer.readInt());
    result.add(buffer.readInt());
    result.add(buffer.readInt());

    buffer.resetReaderIndex();

    result.add(buffer.readInt());
    result.add(buffer.readInt());
    result.add(buffer.readInt());

    //then:
    assertThat(result).isEqualTo(expected);
  }

  @Test
  public void derivedBuffer() {
    // Create buffer with 5 ints and read 2 first values
    final ByteBuf buffer = Unpooled.copyInt(1,2,3,4,5);
    buffer.readInt();
    buffer.readInt();

    assertThat(buffer.readInt()).isEqualTo(3);

    // Derive the buffer and reset reader index to 0
    final ByteBuf derived = buffer.duplicate();
    derived.readerIndex(0);

    // Read the first int from derived view
    assertThat(derived.readInt()).isEqualTo(1);

    // Continue reading ints from the initial buffer
    assertThat(buffer.readInt()).isEqualTo(4);

    // Now let's move writer index to fifth integer array element (4 bytes per integer - 4*4=16) in derived buffer
    derived.writerIndex(16);
    derived.writeInt(7);

    // Initial buffer got updated by the derived view
    assertThat(buffer.readInt()).isEqualTo(7);
  }
}
