package akka.http.websocket.stomp.server.handler.command
import akka.actor.ActorRef
import akka.http.websocket.stomp.bus.event.{LocalEventBus, MessageEvent}
import akka.http.websocket.stomp.parser.{ErrorFrame, SendFrame, StompFrame}
import akka.http.websocket.stomp.server.channel.ChannelRegistry

case class SendCommandHandler() extends CommandHandler {

  def handle(frame: StompFrame, clientConnection: ActorRef): Unit = {
    val sendFrame: SendFrame = frame.asInstanceOf[SendFrame]
    sendFrame.getHeader("destination") match {
      case Some(dh) =>
        ChannelRegistry.getTopic(dh.value) match {
          case Some(bus: LocalEventBus) => bus.publish(MessageEvent(sendFrame))
          case _ => clientConnection ! ErrorFrame(s"No destination found matching ${dh.name}.")
        }
      case None => clientConnection ! ErrorFrame("No destination header found in SEND command.")
    }
  }
}