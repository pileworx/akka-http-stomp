package akka.http.websocket.stomp.test.util

import akka.actor.Props
import akka.http.websocket.stomp.server.channel.StompChannel

class FakeChannel extends StompChannel("/queue/a"){
  def handleMessage(message: String): Option[String] = {
    None
  }
}

object FakeChannel {
  def props = Props[FakeChannel]
}
