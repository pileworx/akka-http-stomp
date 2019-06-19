package akka.http.websocket.stomp.bus.event

import java.util.UUID

import akka.http.websocket.stomp.parser.{MessageFrame, SendFrame, StompHeader}

case class MessageEvent(destination: String,
                        frame: MessageFrame,
                        user: Option[String]) {
  def withSubscriptionId(id: String): MessageEvent = {
    val subIdHeader = StompHeader("subscription", id)
    def headers: Seq[StompHeader] = frame.headers match {
      case Some(hs) => hs :+ subIdHeader
      case None => Seq(subIdHeader)
    }
    val idFrame = frame.copy(headers = Some(headers))
    copy(frame = idFrame)
  }
}

object MessageEvent {
  def apply(frame: SendFrame, user: Option[String] = None): MessageEvent = {
    val ct = frame.getHeader("content-type") match {
      case Some(h) => StompHeader("content-type", h.value)
      case None => StompHeader("content-type", "text/plain")
    }
    val ch = frame.getHeader("destination") match {
      case Some(h) => StompHeader("destination", h.value)
      case None => StompHeader("destination", "")
    }
    val id = StompHeader("message-id", UUID.randomUUID().toString)

    val headers = Seq(ct, id, ch)

    val response = MessageFrame(Some(headers), frame.body)

    MessageEvent(ch.value, response, user)
  }
}
