package akka.http.websocket.stomp.server.handler.command

import akka.actor.ActorRef
import akka.http.websocket.stomp.bus.event.LocalEventBus
import akka.http.websocket.stomp.parser.{ErrorFrame, StompFrame}
import akka.http.websocket.stomp.server.channel.{ChannelRegistry, Unsubscribe}

case class UnsubscribeCommandHandler() extends CommandHandler {
  def handle(frame: StompFrame, clientConnection: ActorRef): Unit = {
    frame.getHeader("destination") match {
      case Some(dh) => frame.getHeader("id") match {
        case Some(id) => ChannelRegistry.getTopic(dh.value) match {
          case Some(bus: LocalEventBus) =>
            bus.unsubscribe(clientConnection, true)
            clientConnection ! Unsubscribe(dh.value, id.value)
          case None => clientConnection ! ErrorFrame(s"No topic found matching ${dh.name}.")
        }
        case None => clientConnection ! ErrorFrame("No id header found in UNSUBSCRIBE command.")
      }
      case None => Unit
    }
  }
}