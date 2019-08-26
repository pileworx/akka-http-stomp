package akka.http.websocket.stomp.server.handler.command
import akka.actor.ActorRef
import akka.http.websocket.stomp.bus.event.LocalEventBus
import akka.http.websocket.stomp.parser.{ErrorFrame, StompFrame}
import akka.http.websocket.stomp.server.channel.ChannelRegistry
import akka.http.websocket.stomp.server.channel.command.Subscribe

case class SubscribeCommandHandler() extends CommandHandler {
  def handle(frame: StompFrame, clientConnection: ActorRef): Unit = {
    frame.header("destination") match {
      case Some(dh) => frame.header("id") match {
        case Some(id) => ChannelRegistry.topic(dh.value) match {
          case Some(bus: LocalEventBus) =>
            bus.subscribe(clientConnection, true)
            clientConnection ! Subscribe(dh.value, id.value)
          case _ => clientConnection ! ErrorFrame(s"No destination found matching ${dh.name}.")
        }
        case None => clientConnection ! ErrorFrame("No id header found in SUBSCRIBE command.")
      }
      case None => clientConnection ! ErrorFrame("No destination header found in SUBSCRIBE command.")
    }
  }
}