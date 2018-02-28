package com.github.wololock.netty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class BlockingIOExample {

  public static void main(String[] args) throws IOException {
    // blocks until a connection is established on the ServerSocket b , then
    // returns a new Socket for communication between the client and the server.
    // The ServerSocket then resumes listening for incoming connections.
    final ServerSocket serverSocket = new ServerSocket(6767);

    final Socket clientSocket = serverSocket.accept();
    final BufferedReader in = new BufferedReader(
      new InputStreamReader(clientSocket.getInputStream())
    );
    final PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

    String request, response;

    // blocks until a string terminated by a linefeed or carriage return is read in
    while ((request = in.readLine()) != null) {
      if ("Done".equals(request)) {
        break;
      }
      response = processResponse(request);
      out.println(response);
    }
  }

  private static String processResponse(String request) {
    return request.toUpperCase();
  }
}
