package akka.http.websocket.stomp.server.handler.command

import akka.http.websocket.stomp.parser.{StompFrame, StompHeader}
import akka.http.websocket.stomp.parser.StompCommand._

case class DisconnectCommandHandler() extends CommandHandler{
  def handle(frame: StompFrame): Option[StompFrame] = {
    frame.getHeader("receipt") match {
      case Some(rh) =>
        val headers = Some(Seq(StompHeader("receipt-id", rh.value)))
        Some(StompFrame(RECEIPT, headers, None))
      case None => None
    }
  }
}
