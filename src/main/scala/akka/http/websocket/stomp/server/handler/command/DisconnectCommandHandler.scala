package akka.http.websocket.stomp.server.handler.command

import akka.actor.ActorRef
import akka.http.websocket.stomp.parser.{ReceiptFrame, StompFrame, StompHeader}
import akka.http.websocket.stomp.server.channel.command.TerminateConnection

case class DisconnectCommandHandler() extends CommandHandler{

  def handle(frame: StompFrame, clientConnection: ActorRef): Unit = {
    handleReceipt(frame, clientConnection)
    clientConnection ! TerminateConnection
  }

  def handleReceipt(frame: StompFrame, clientConnection: ActorRef): Unit = {
    frame.getHeader("receipt").foreach { rh =>
      clientConnection ! ReceiptFrame(Seq(StompHeader("receipt-id", rh.value)))
    }
  }
}