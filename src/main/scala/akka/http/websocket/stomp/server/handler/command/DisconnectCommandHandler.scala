package akka.http.websocket.stomp.server.handler.command

import akka.actor.ActorRef
import akka.http.websocket.stomp.parser.{ReceiptFrame, StompFrame, StompHeader}
import akka.http.websocket.stomp.server.channel.TerminateConnection

case class DisconnectCommandHandler() extends CommandHandler{

  def handle(frame: StompFrame, clientConnection: ActorRef): Unit = {
    frame.getHeader("receipt") match {
      case Some(rh) =>
        val headers = Some(Seq(StompHeader("receipt-id", rh.value)))
        clientConnection ! ReceiptFrame(headers)
    }

    clientConnection ! TerminateConnection
  }
}