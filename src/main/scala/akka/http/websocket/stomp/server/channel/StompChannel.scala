package akka.http.websocket.stomp.server.channel

import akka.actor.{Actor, ActorRef}
import akka.http.websocket.stomp.parser.StompCommand._
import akka.http.websocket.stomp.parser.StompFrame

import scala.collection.mutable

abstract class StompChannel(val channel: String,
                            val subscribable: Boolean = false) extends Actor {

  final def receive: Receive = {
    case frame:StompFrame => frame match {
      case StompFrame(SEND, _, _) => sender ! extractAndHandle(frame)
      case _ => sender ! None
    }
  }

  private def extractAndHandle(frame: StompFrame) = frame.body match {
    case Some(body) => handleMessage(body)
    case None => Some(StompFrame.errorFrame("SEND command missing body."))
  }

  protected def handleMessage(message: String): Option[String]
}

object StompChannel {

  private val channels: mutable.Map[String, ActorRef]  = mutable.Map()

  def addChannel(channel: (String, ActorRef)*): Unit = channels ++= channel

  def getChannel(channel: String): Option[ActorRef] = {
    channels.get(channel)
  }
}
