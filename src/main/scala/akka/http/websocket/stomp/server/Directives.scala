package akka.http.websocket.stomp.server

import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.websocket.stomp.parser.{FrameWriter, TextFrameParser}
import akka.http.websocket.stomp.server.handler.command.HandlerResolver
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink}

object Directives {

  private val commandHandler = new HandlerResolver

  private def stompFlow(implicit materializer: Materializer): Flow[Message, Message, Any] = {
    val flow = Flow[Message].mapConcat {
      case TextMessage.Strict(tm) =>
        TextMessage(new FrameWriter().write(commandHandler.handle(new TextFrameParser(tm).parse()))) :: Nil
      case bm: BinaryMessage =>
        bm.dataStream.runWith(Sink.ignore)
        Nil
    }

    flow
  }

  def stomp(implicit materializer: Materializer): Route = {
    handleWebSocketMessagesForOptionalProtocol(stompFlow, Some("v12.stomp")) ~
    handleWebSocketMessagesForOptionalProtocol(stompFlow, Some("v11.stomp")) ~
    handleWebSocketMessagesForOptionalProtocol(stompFlow, Some("v10.stomp"))
  }
}
