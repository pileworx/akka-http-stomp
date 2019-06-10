package akka.http.websocket.stomp.server.handler.command
import akka.http.websocket.stomp.parser.StompCommand._
import akka.http.websocket.stomp.parser.StompFrame

class HandlerResolver extends CommandHandler {

  override def handle(frame: StompFrame): Option[StompFrame] = {
    if(HandlerResolver.handlers.contains(frame.command))
      HandlerResolver.handlers(frame.command).handle(frame)
    else
      Some(StompFrame.errorFrame(s"Command ${frame.command.toString} is not supported"))
  }
}

object HandlerResolver {
  private val handlers = Map(
    CONNECT -> ConnectCommandHandler(),
    SEND -> SendCommandHandler(),
    DISCONNECT -> DisconnectCommandHandler()
  )
}
