package akka.http.websocket.stomp.parser

trait FrameParser[T] {
  def parse(): StompFrame
}