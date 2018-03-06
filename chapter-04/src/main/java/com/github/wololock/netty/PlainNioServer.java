package com.github.wololock.netty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class PlainNioServer {

  public void serve(int port) throws IOException {
    final ServerSocketChannel serverChannel = ServerSocketChannel.open();
    serverChannel.configureBlocking(false);

    final ServerSocket serverSocket = serverChannel.socket();
    final InetSocketAddress address = new InetSocketAddress(port);
    serverSocket.bind(address);

    final Selector selector = Selector.open();
    serverChannel.register(selector, SelectionKey.OP_ACCEPT);

    final ByteBuffer msg = ByteBuffer.wrap("Hi!\r\n".getBytes(Charset.forName("UTF-8")));

    for (;;) {
      try {
        selector.select();
      } catch (IOException e) {
        e.printStackTrace();
        break;
      }

      final Set<SelectionKey> readyKeys = selector.selectedKeys();
      final Iterator<SelectionKey> iterator = readyKeys.iterator();

      while (iterator.hasNext()) {
        final SelectionKey key = iterator.next();
        iterator.remove();

        try {
          if (key.isAcceptable()) {
            final ServerSocketChannel server = (ServerSocketChannel) key.channel();
            final SocketChannel client = server.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, msg.duplicate());
            System.out.println("Accepted connection from " + client);
          }

          if (key.isWritable()) {
            final SocketChannel client = (SocketChannel) key.channel();
            final ByteBuffer buffer = (ByteBuffer) key.attachment();
            while (buffer.hasRemaining()) {
              if (client.write(buffer) == 0) {
                break;
              }
            }
            client.close();
          }
        } catch (IOException e) {
          key.cancel();
          try {
            key.channel().close();
          } catch (IOException ex) {

          }
        }
      }
    }
  }

  public static void main(String[] args) throws IOException {
    new PlainNioServer().serve(8999);
  }
}
