package akka.http.websocket.stomp.server.handler.command

import akka.http.websocket.stomp.parser.StompFrame

trait CommandHandler {
  def handle(frame: StompFrame): Option[StompFrame]
}
