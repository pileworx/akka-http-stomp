package akka.http.websocket.stomp.server.handler.command
import akka.http.websocket.stomp.parser.StompFrame
import akka.http.websocket.stomp.server.channel.StompChannel
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

case class SendCommandHandler() extends CommandHandler {

  implicit val timeout: Timeout = Timeout(1 minutes)

  def handle(frame: StompFrame): Option[StompFrame] = {
    frame.getHeader("destination") match {
      case Some(dh) => StompChannel.getChannel(dh.value) match {
        case Some(channel) =>
          val response = Await.result(channel ? frame, timeout.duration).asInstanceOf[Option[String]]
          response match {
            case None => None
          }
        case None => Some(StompFrame.errorFrame(s"No channel found matching ${dh.name}."))
      }
      case None => Some(StompFrame.errorFrame("No destination header found in SEND command."))
    }
  }
}