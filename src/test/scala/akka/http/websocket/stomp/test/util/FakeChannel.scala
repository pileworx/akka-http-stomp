package akka.http.websocket.stomp.test.util

import akka.http.websocket.stomp.server.channel.StompChannel

case class FakeMessage(one: String, two: String)

class FakeChannel extends StompChannel[FakeMessage]{
  override protected def handleMessage(message: FakeMessage): Unit = ???
}
