package akka.http.websocket.stomp.server.handler.command

import akka.actor.ActorRef
import akka.http.websocket.stomp.parser.StompFrame

trait CommandHandler {
  def handle(frame: StompFrame, clientConnection: ActorRef): Unit
}