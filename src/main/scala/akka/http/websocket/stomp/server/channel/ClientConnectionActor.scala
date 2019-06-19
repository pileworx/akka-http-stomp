package akka.http.websocket.stomp.server.channel

import akka.actor.{Actor, ActorSystem}
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.http.websocket.stomp.bus.event.MessageEvent
import akka.http.websocket.stomp.parser.{FrameWriter, StompClientFrame, StompServerFrame, TextFrameParser}
import akka.http.websocket.stomp.server.Directives.GetWebsocketFlow
import akka.http.websocket.stomp.server.handler.command.CommandHandler
import akka.stream.scaladsl.GraphDSL.Implicits._
import akka.stream.scaladsl.{Flow, GraphDSL, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, FlowShape, OverflowStrategy}

import scala.collection.mutable
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class ClientConnectionActor(private val commandHandler: CommandHandler,
                            private val frameWriter: FrameWriter)
                           (implicit val system: ActorSystem,
                            implicit val materializer: ActorMaterializer,
                            implicit val exctx: ExecutionContext) extends Actor {

  val (client, publisher) = Source
    .actorRef[String](1000, OverflowStrategy.fail)
    .toMat(Sink.asPublisher(fanout = false))(Keep.both)
    .run()
  val subscriptions: mutable.Map[String, mutable.ListBuffer[String]] = mutable.Map()
  var user: Option[User] = None

  def receive: Receive = {
    case GetWebsocketFlow =>

      val flow = Flow.fromGraph(GraphDSL.create() { implicit b =>
        val textMsgFlow = b.add(Flow[Message]
          .mapAsync(1) {
            case tm: TextMessage => tm.toStrict(3.seconds).map { m =>
              new TextFrameParser(m.text).parse()
            }
            case bm: BinaryMessage =>
              bm.dataStream.runWith(Sink.ignore)
              Future.failed(new Exception("Binary messages are not supported."))
          })

        val pubSrc = b.add(Source.fromPublisher(publisher).map(TextMessage(_)))

        textMsgFlow ~> Sink.foreach[Any](self ! _)
        FlowShape(textMsgFlow.in, pubSrc.out)
      })

      sender ! flow

    case clientFrame: StompClientFrame => commandHandler.handle(clientFrame, self)
    case serverFrame: StompServerFrame => client ! frameWriter.write(serverFrame)
    case TerminateConnection => context.stop(self)
    case subscribe: Subscribe => addSubscription(subscribe)
    case unsubscribe: Unsubscribe => subscriptions -= unsubscribe.topic
    case messageEvent: MessageEvent => messageEvent.user match {
      case Some(u) => user match {
        case Some(mu) =>
          if (u.equalsIgnoreCase(mu.username)) sendMessage(messageEvent)
      }
      case None => sendMessage(messageEvent)
    }
  }

  private def sendMessage(me: MessageEvent): Unit = {
    subscriptions(me.destination).foreach(id => self ! me.withSubscriptionId(id).frame)
  }

  private def addSubscription(s: Subscribe): Unit = {
    if (!subscriptions.contains(s.topic))
      subscriptions += s.topic -> mutable.ListBuffer()

    if (!subscriptions(s.topic).contains(s.id))
      subscriptions(s.topic) += s.id
  }
}