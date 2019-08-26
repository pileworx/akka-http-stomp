package akka.http.websocket.stomp.bus.event

import akka.http.websocket.stomp.parser.{MessageFrame, SendFrame, StompHeader}
import java.util.UUID

case class MessageEvent(destination: String,
                        headers: Seq[StompHeader],
                        body: Option[String],
                        user: Option[String]) {

  def frame: MessageFrame = MessageFrame(headers, body)

  def withSubscriptionId(id: String): MessageEvent = copy(headers = headers :+ StompHeader("subscription", id))
}

object MessageEvent {
  private[this] val contentType = "content-type"
  private[this] val destination = "destination"

  def apply(frame: SendFrame, user: Option[String] = None): MessageEvent = {
    val ct = frame.header(contentType) match {
      case Some(h) => StompHeader(contentType, h.value)
      case None => StompHeader(contentType, "text/plain")
    }
    val ch = frame.header(destination) match {
      case Some(h) => StompHeader(destination, h.value)
      case None => StompHeader(destination, "")
    }
    val id = StompHeader("message-id", UUID.randomUUID().toString)

    val headers = Seq(ct, id, ch)

    MessageEvent(ch.value, headers, frame.body, user)
  }
}
