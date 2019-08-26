package akka.http.websocket.stomp.server.handler.command

import akka.actor.ActorRef
import akka.http.websocket.stomp.bus.event.LocalEventBus
import akka.http.websocket.stomp.parser.{ErrorFrame, StompFrame}
import akka.http.websocket.stomp.server.channel.ChannelRegistry
import akka.http.websocket.stomp.server.channel.command.Unsubscribe

case class UnsubscribeCommandHandler() extends CommandHandler {
  def handle(frame: StompFrame, clientConnection: ActorRef): Unit = {
    frame.header("destination") match {
      case Some(dh) => frame.header("id") match {
        case Some(id) => ChannelRegistry.getTopic(dh.value) match {
          case Some(bus: LocalEventBus) =>
            bus.unsubscribe(clientConnection, true)
            clientConnection ! Unsubscribe(dh.value, id.value)
          case _ => clientConnection ! ErrorFrame(s"No topic found matching ${dh.name}.")
        }
        case None => clientConnection ! ErrorFrame("No id header found in UNSUBSCRIBE command.")
      }
      case None => Unit
    }
  }
}