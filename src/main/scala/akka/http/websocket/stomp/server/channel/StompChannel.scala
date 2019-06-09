package akka.http.websocket.stomp.server.channel

import akka.actor.Actor

abstract class StompChannel[T] extends Actor {
  override def receive: Receive = ???

  protected def handleMessage(message: T): Unit
}
