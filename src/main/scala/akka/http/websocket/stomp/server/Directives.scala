package akka.http.websocket.stomp.server

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.ws.Message
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.websocket.stomp.parser.FrameWriter
import akka.http.websocket.stomp.server.channel.{ChannelRegistry, ClientConnectionActor}
import akka.http.websocket.stomp.server.handler.command.HandlerResolver
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object Directives {

  case object GetWebsocketFlow

  private val commandHandler = new HandlerResolver
  private val frameWriter = new FrameWriter

  def stomp(topics: Seq[String] = Seq(),
            queues: Map[String, ActorRef] = Map())
           (implicit system: ActorSystem,
            actorMaterializer: ActorMaterializer,
            exctx: ExecutionContext): Route = {

    ChannelRegistry.topics(topics)

    val handler = system.actorOf(Props(new ClientConnectionActor(commandHandler, frameWriter)))
    val futureFlow = (handler ? GetWebsocketFlow) (3.seconds).mapTo[Flow[Message, Message, _]]

    onComplete(futureFlow) {
      case Success(flow) =>
        handleWebSocketMessagesForOptionalProtocol(flow, Some("v12.stomp")) ~
        handleWebSocketMessagesForOptionalProtocol(flow, Some("v11.stomp")) ~
        handleWebSocketMessagesForOptionalProtocol(flow, Some("v10.stomp"))
      case Failure(err) => complete(err.toString)
    }
  }
}
