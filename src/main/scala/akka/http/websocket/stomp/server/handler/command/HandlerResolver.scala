package akka.http.websocket.stomp.server.handler.command
import akka.actor.ActorRef
import akka.http.websocket.stomp.parser.StompCommand._
import akka.http.websocket.stomp.parser.{ErrorFrame, StompFrame}

class HandlerResolver extends CommandHandler {

  def handle(frame: StompFrame, clientConnection: ActorRef): Unit = {
    if(HandlerResolver.handlers.contains(frame.command))
      HandlerResolver.handlers(frame.command).handle(frame, clientConnection)
    else
      clientConnection ! ErrorFrame(s"Command ${frame.command.toString} is not supported")
  }
}

object HandlerResolver {
  private val handlers = Map(
    CONNECT -> ConnectCommandHandler(),
    STOMP -> ConnectCommandHandler(),
    SEND -> SendCommandHandler(),
    SUBSCRIBE -> SubscribeCommandHandler(),
    UNSUBSCRIBE -> UnsubscribeCommandHandler(),
    DISCONNECT -> DisconnectCommandHandler()
  )
}
