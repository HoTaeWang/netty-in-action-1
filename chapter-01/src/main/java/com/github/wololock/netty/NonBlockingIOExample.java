package com.github.wololock.netty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NonBlockingIOExample {

  public static void main(String[] args) throws IOException {
    final Selector selector = Selector.open();

    final ServerSocketChannel serverSocket = ServerSocketChannel.open();
    serverSocket.bind(new InetSocketAddress("localhost", 5757));
    serverSocket.configureBlocking(false);
    serverSocket.register(selector, SelectionKey.OP_ACCEPT);

    final ByteBuffer buffer = ByteBuffer.allocate(256);

    while (true) {
      selector.select();

      final Set<SelectionKey> selectedKeys = selector.selectedKeys();
      final Iterator<SelectionKey> iter = selectedKeys.iterator();

      while (iter.hasNext()) {
        final SelectionKey key = iter.next();

        if (key.isAcceptable()) {
          register(selector, serverSocket);
        }

        if (key.isReadable()) {
          echo(buffer, key);
        }

        iter.remove();
      }
    }
  }

  private static void echo(final ByteBuffer buffer, final SelectionKey key) throws IOException {
    final SocketChannel client = (SocketChannel) key.channel();
    client.read(buffer);

    if ("Done".equals(new String(buffer.array()).trim())) {
      client.close();
      System.out.println("Not accepting client messages anymore.");
    }

    buffer.flip();
    client.write(buffer);
    buffer.clear();
  }

  private static void register(final Selector selector, final ServerSocketChannel serverSocket) throws IOException {
    final SocketChannel client = serverSocket.accept();
    client.configureBlocking(false);
    client.register(selector, SelectionKey.OP_READ);
  }
}
