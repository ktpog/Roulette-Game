package Server;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

@WebSocket
public class WebSocketHandler {
  // Store sessions if you want to, for example, broadcast a message to all users
  static Map<Session, Session> sessionMap = new ConcurrentHashMap<>();
  static String clickCountString = "0";


  public static void broadcast(String message) {
    sessionMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
      try {
        session.getRemote().sendString(message);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  @OnWebSocketConnect
  public void connected(Session session) throws IOException {
    sessionMap.put(session, session);
    session.getRemote().sendString(clickCountString); // and send it back
  }

  @OnWebSocketClose
  public void closed(Session session, int statusCode, String reason) {
    sessionMap.remove(session);
  }

  @OnWebSocketMessage
  public void message(Session session, String message) throws IOException {
    clickCountString = message; // save the count
    broadcast(message);
  }
}
